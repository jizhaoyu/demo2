package com.example.booktrading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booktrading.entity.dto.AiChatDTO;
import com.example.booktrading.entity.po.Book;
import com.example.booktrading.entity.po.Category;
import com.example.booktrading.entity.po.Orders;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.AiChatVO;
import com.example.booktrading.mapper.BookMapper;
import com.example.booktrading.mapper.CategoryMapper;
import com.example.booktrading.mapper.OrderMapper;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.AiService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI智能助手服务实现
 * 集成智谱GLM-4模型，支持知识库、实时数据库查询和记忆功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    
    @Value("${ai.zhipu.api-key:}")
    private String apiKey;
    
    @Value("${ai.zhipu.model:glm-4-flash}")
    private String model;
    
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    
    // 知识库内容
    private String knowledgeBase = "";
    
    // 会话记忆存储
    private final Map<String, List<AiChatDTO.ChatMessage>> sessionMemory = new ConcurrentHashMap<>();
    
    // 最大记忆轮数
    private static final int MAX_MEMORY_ROUNDS = 10;
    
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    
    private final Gson gson = new Gson();
    
    @PostConstruct
    public void init() {
        initKnowledgeBase();
    }
    
    @Override
    public void initKnowledgeBase() {
        StringBuilder kb = new StringBuilder();
        
        // 平台基础信息
        kb.append("【平台信息】\n");
        kb.append("这是一个二手书交易平台，用户可以在这里买卖二手书籍。\n");
        kb.append("主要功能：用户注册登录、发布书籍、浏览搜索、收藏书籍、下单购买、消息沟通、订单管理、评价系统。\n\n");
        
        // 平台使用指南
        kb.append("【使用指南】\n");
        kb.append("1. 发布书籍：登录后点击\"发布书籍\"，填写书名、作者、价格、成色、描述，上传图片即可。\n");
        kb.append("2. 购买书籍：浏览书籍列表，点击感兴趣的书籍查看详情，点击\"立即购买\"填写收货地址下单。\n");
        kb.append("3. 联系卖家：在书籍详情页点击\"联系卖家\"可以发送消息咨询。\n");
        kb.append("4. 收藏书籍：点击\"收藏\"按钮可以收藏喜欢的书籍，在\"我的收藏\"中查看。\n");
        kb.append("5. 订单流程：下单→卖家确认→买家支付→卖家发货→买家确认收货→完成交易。\n");
        kb.append("6. 评价系统：交易完成后可以互相评价，评价会影响用户信誉。\n\n");
        
        // 常见问题
        kb.append("【常见问题】\n");
        kb.append("Q: 平台收费吗？A: 目前平台完全免费，不收取任何手续费。\n");
        kb.append("Q: 如何保证交易安全？A: 建议使用平台消息功能沟通，确认收货后再完成交易。\n");
        kb.append("Q: 书籍成色如何判断？A: 全新(未拆封)、九成新(几乎无使用痕迹)、八成新(轻微使用痕迹)、七成新(有使用痕迹)、六成新及以下。\n");
        
        knowledgeBase = kb.toString();
        log.info("知识库初始化完成");
    }
    
    @Override
    public AiChatVO chat(AiChatDTO dto) {
        String sessionId = dto.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        
        List<AiChatDTO.ChatMessage> memory = sessionMemory.computeIfAbsent(sessionId, k -> new ArrayList<>());
        
        if (dto.getHistory() != null && !dto.getHistory().isEmpty()) {
            memory.clear();
            memory.addAll(dto.getHistory());
        }
        
        try {
            // 根据用户问题查询相关数据
            String contextData = queryRelevantData(dto.getMessage());
            
            // 构建系统提示词
            String systemPrompt = buildSystemPrompt(dto.getCurrentPage(), contextData);
            
            // 调用AI API
            String reply = callZhipuApi(systemPrompt, dto.getMessage(), memory);
            
            // 保存到记忆
            AiChatDTO.ChatMessage userMsg = new AiChatDTO.ChatMessage();
            userMsg.setRole("user");
            userMsg.setContent(dto.getMessage());
            memory.add(userMsg);
            
            AiChatDTO.ChatMessage assistantMsg = new AiChatDTO.ChatMessage();
            assistantMsg.setRole("assistant");
            assistantMsg.setContent(reply);
            memory.add(assistantMsg);
            
            while (memory.size() > MAX_MEMORY_ROUNDS * 2) {
                memory.remove(0);
                memory.remove(0);
            }
            
            return AiChatVO.success(reply, sessionId);
            
        } catch (Exception e) {
            log.error("AI对话失败", e);
            return AiChatVO.error("抱歉，我暂时无法回答，请稍后再试。");
        }
    }
    
    /**
     * 根据用户问题查询相关数据库数据
     */
    private String queryRelevantData(String message) {
        StringBuilder data = new StringBuilder();
        String msg = message.toLowerCase();
        
        try {
            // 优先检查价格范围查询（如"50元以下的书"）
            String priceResult = queryBooksByPriceRange(message);
            if (!priceResult.isEmpty()) {
                data.append(priceResult);
                return data.toString(); // 价格查询优先返回
            }
            
            // 查询统计数据
            if (containsAny(msg, "多少", "数量", "统计", "总共", "有几")) {
                data.append(queryStatistics());
            }
            
            // 查询分类相关
            if (containsAny(msg, "分类", "类别", "种类", "类型")) {
                data.append(queryCategoryData());
            }
            
            // 查询书籍相关
            if (containsAny(msg, "书", "推荐", "便宜", "最贵", "热门", "畅销", "新书")) {
                data.append(queryBookData(msg));
            }
            
            // 查询价格相关
            if (containsAny(msg, "价格", "多少钱", "价位", "便宜", "贵")) {
                data.append(queryPriceData(msg));
            }
            
            // 查询特定书名
            String bookTitle = extractBookTitle(message);
            if (bookTitle != null) {
                data.append(querySpecificBook(bookTitle));
            }
            
            // 查询特定作者
            String author = extractAuthor(message);
            if (author != null) {
                data.append(queryBooksByAuthor(author));
            }
            
        } catch (Exception e) {
            log.warn("查询数据库失败", e);
        }
        
        return data.toString();
    }
    
    /**
     * 根据价格范围查询书籍（增强版）
     */
    private String queryBooksByPriceRange(String message) {
        StringBuilder sb = new StringBuilder();
        Integer maxPrice = null;
        Integer minPrice = null;
        
        // 匹配多种价格表达方式
        // "50元以下的书"、"50以下"、"50块以下"、"不超过50"、"低于50"
        Pattern maxPattern = Pattern.compile("(\\d+)\\s*(?:元|块|块钱)?\\s*(?:以下|以内|内|之内)");
        Matcher maxMatcher = maxPattern.matcher(message);
        if (maxMatcher.find()) {
            maxPrice = Integer.parseInt(maxMatcher.group(1));
        }
        
        // "不超过xx"、"低于xx"
        Pattern maxPattern2 = Pattern.compile("(?:不超过|低于|小于)\\s*(\\d+)");
        Matcher maxMatcher2 = maxPattern2.matcher(message);
        if (maxMatcher2.find()) {
            maxPrice = Integer.parseInt(maxMatcher2.group(1));
        }
        
        // "50元以上"、"50以上"
        Pattern minPattern = Pattern.compile("(\\d+)\\s*(?:元|块|块钱)?\\s*(?:以上|起|起步)");
        Matcher minMatcher = minPattern.matcher(message);
        if (minMatcher.find()) {
            minPrice = Integer.parseInt(minMatcher.group(1));
        }
        
        // "高于xx"、"大于xx"
        Pattern minPattern2 = Pattern.compile("(?:高于|大于|超过)\\s*(\\d+)");
        Matcher minMatcher2 = minPattern2.matcher(message);
        if (minMatcher2.find()) {
            minPrice = Integer.parseInt(minMatcher2.group(1));
        }
        
        // "20到50元"、"20-50"
        Pattern rangePattern = Pattern.compile("(\\d+)\\s*(?:元|块)?\\s*(?:到|至|-|~)\\s*(\\d+)");
        Matcher rangeMatcher = rangePattern.matcher(message);
        if (rangeMatcher.find()) {
            minPrice = Integer.parseInt(rangeMatcher.group(1));
            maxPrice = Integer.parseInt(rangeMatcher.group(2));
        }
        
        // 如果提取到了价格条件，查询数据库
        if (maxPrice != null || minPrice != null) {
            LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, 1);
            
            String priceDesc = "";
            if (minPrice != null && maxPrice != null) {
                wrapper.ge(Book::getPrice, minPrice).le(Book::getPrice, maxPrice);
                priceDesc = minPrice + "-" + maxPrice + "元";
            } else if (maxPrice != null) {
                wrapper.le(Book::getPrice, maxPrice);
                priceDesc = maxPrice + "元以下";
            } else {
                wrapper.ge(Book::getPrice, minPrice);
                priceDesc = minPrice + "元以上";
            }
            
            wrapper.orderByAsc(Book::getPrice).last("LIMIT 10");
            
            List<Book> books = bookMapper.selectList(wrapper);
            
            if (!books.isEmpty()) {
                sb.append("\n【").append(priceDesc).append("的书籍，共").append(books.size()).append("本】\n");
                for (int i = 0; i < books.size(); i++) {
                    Book b = books.get(i);
                    sb.append(String.format("%d. 《%s》- %s\n   💰 %.2f元 | %s\n",
                        i + 1, b.getTitle(), b.getAuthor(), b.getPrice(), getConditionDesc(b.getConditionLevel())));
                }
            } else {
                sb.append("\n【查询结果】\n");
                sb.append("抱歉，暂无").append(priceDesc).append("的书籍在售。\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 查询平台统计数据
     */
    private String queryStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n【平台实时统计】\n");
        
        // 用户统计
        Long totalUsers = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getStatus, 1));
        sb.append("• 注册用户数：").append(totalUsers).append("人\n");
        
        // 书籍统计
        Long totalBooks = bookMapper.selectCount(null);
        Long onSaleBooks = bookMapper.selectCount(new LambdaQueryWrapper<Book>().eq(Book::getStatus, 1));
        sb.append("• 书籍总数：").append(totalBooks).append("本\n");
        sb.append("• 在售书籍：").append(onSaleBooks).append("本\n");
        
        // 订单统计
        Long totalOrders = orderMapper.selectCount(null);
        Long completedOrders = orderMapper.selectCount(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, 5));
        sb.append("• 订单总数：").append(totalOrders).append("笔\n");
        sb.append("• 已完成交易：").append(completedOrders).append("笔\n");
        
        // 分类统计
        Long categoryCount = categoryMapper.selectCount(null);
        sb.append("• 书籍分类：").append(categoryCount).append("个\n");
        
        return sb.toString();
    }
    
    /**
     * 查询分类数据
     */
    private String queryCategoryData() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n【书籍分类信息】\n");
        
        List<Category> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)
        );
        
        for (Category cat : categories) {
            int bookCount = bookMapper.countByCategoryId(cat.getId());
            sb.append("• ").append(cat.getName()).append("：").append(bookCount).append("本书籍\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 查询书籍数据
     */
    private String queryBookData(String msg) {
        StringBuilder sb = new StringBuilder();
        
        // 热门书籍（按浏览量）
        if (containsAny(msg, "热门", "畅销", "受欢迎")) {
            sb.append("\n【热门书籍TOP5】\n");
            List<Book> hotBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                    .eq(Book::getStatus, 1)
                    .orderByDesc(Book::getViewCount)
                    .last("LIMIT 5")
            );
            for (int i = 0; i < hotBooks.size(); i++) {
                Book b = hotBooks.get(i);
                sb.append(String.format("%d. 《%s》- %s，%.2f元，浏览%d次\n",
                    i + 1, b.getTitle(), b.getAuthor(), b.getPrice(), b.getViewCount()));
            }
        }
        
        // 最新上架
        if (containsAny(msg, "新书", "最新", "刚上架", "新上")) {
            sb.append("\n【最新上架书籍】\n");
            List<Book> newBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                    .eq(Book::getStatus, 1)
                    .orderByDesc(Book::getCreateTime)
                    .last("LIMIT 5")
            );
            for (Book b : newBooks) {
                sb.append(String.format("• 《%s》- %s，%.2f元，%s\n",
                    b.getTitle(), b.getAuthor(), b.getPrice(), getConditionDesc(b.getConditionLevel())));
            }
        }
        
        // 便宜书籍
        if (containsAny(msg, "便宜", "低价", "实惠", "划算")) {
            sb.append("\n【超值低价书籍】\n");
            List<Book> cheapBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                    .eq(Book::getStatus, 1)
                    .orderByAsc(Book::getPrice)
                    .last("LIMIT 5")
            );
            for (Book b : cheapBooks) {
                sb.append(String.format("• 《%s》仅%.2f元（原价%.2f元）\n",
                    b.getTitle(), b.getPrice(), b.getOriginalPrice()));
            }
        }
        
        // 全新书籍
        if (containsAny(msg, "全新", "新书", "未拆封")) {
            sb.append("\n【全新品相书籍】\n");
            List<Book> newConditionBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                    .eq(Book::getStatus, 1)
                    .eq(Book::getConditionLevel, 1)
                    .last("LIMIT 5")
            );
            for (Book b : newConditionBooks) {
                sb.append(String.format("• 《%s》- %s，%.2f元\n",
                    b.getTitle(), b.getAuthor(), b.getPrice()));
            }
        }
        
        // 推荐书籍（综合推荐）
        if (containsAny(msg, "推荐", "有什么好书", "看什么")) {
            sb.append("\n【精选推荐书籍】\n");
            List<Book> recommendBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                    .eq(Book::getStatus, 1)
                    .le(Book::getConditionLevel, 2) // 九成新以上
                    .orderByDesc(Book::getViewCount)
                    .last("LIMIT 5")
            );
            for (Book b : recommendBooks) {
                sb.append(String.format("• 《%s》- %s，%.2f元，%s\n",
                    b.getTitle(), b.getAuthor(), b.getPrice(), getConditionDesc(b.getConditionLevel())));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 查询价格相关数据
     */
    private String queryPriceData(String msg) {
        StringBuilder sb = new StringBuilder();
        
        // 提取价格范围
        Integer minPrice = extractNumber(msg, "以上", "起");
        Integer maxPrice = extractNumber(msg, "以下", "内", "以内");
        
        if (minPrice != null || maxPrice != null) {
            sb.append("\n【符合价格条件的书籍】\n");
            LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, 1);
            
            if (minPrice != null) {
                wrapper.ge(Book::getPrice, minPrice);
            }
            if (maxPrice != null) {
                wrapper.le(Book::getPrice, maxPrice);
            }
            wrapper.orderByAsc(Book::getPrice).last("LIMIT 8");
            
            List<Book> books = bookMapper.selectList(wrapper);
            for (Book b : books) {
                sb.append(String.format("• 《%s》- %.2f元\n", b.getTitle(), b.getPrice()));
            }
            
            if (books.isEmpty()) {
                sb.append("暂无符合该价格范围的书籍\n");
            }
        }
        
        // 价格统计
        if (containsAny(msg, "平均", "一般多少")) {
            List<Book> allBooks = bookMapper.selectList(
                new LambdaQueryWrapper<Book>().eq(Book::getStatus, 1)
            );
            if (!allBooks.isEmpty()) {
                double avgPrice = allBooks.stream()
                    .mapToDouble(b -> b.getPrice().doubleValue())
                    .average().orElse(0);
                double minP = allBooks.stream()
                    .mapToDouble(b -> b.getPrice().doubleValue())
                    .min().orElse(0);
                double maxP = allBooks.stream()
                    .mapToDouble(b -> b.getPrice().doubleValue())
                    .max().orElse(0);
                
                sb.append("\n【价格统计】\n");
                sb.append(String.format("• 平均价格：%.2f元\n", avgPrice));
                sb.append(String.format("• 最低价格：%.2f元\n", minP));
                sb.append(String.format("• 最高价格：%.2f元\n", maxP));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 查询特定书籍
     */
    private String querySpecificBook(String bookTitle) {
        StringBuilder sb = new StringBuilder();
        
        List<Book> books = bookMapper.selectList(
            new LambdaQueryWrapper<Book>()
                .like(Book::getTitle, bookTitle)
                .eq(Book::getStatus, 1)
        );
        
        if (!books.isEmpty()) {
            sb.append("\n【查询到的书籍】\n");
            for (Book b : books) {
                User seller = userMapper.selectById(b.getUserId());
                String sellerName = seller != null ? seller.getUsername() : "未知";
                sb.append(String.format("• 《%s》\n  作者：%s\n  价格：%.2f元（原价%.2f元）\n  成色：%s\n  卖家：%s\n  描述：%s\n\n",
                    b.getTitle(), b.getAuthor(), b.getPrice(), b.getOriginalPrice(),
                    getConditionDesc(b.getConditionLevel()), sellerName,
                    b.getDescription() != null ? b.getDescription() : "无"));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 按作者查询书籍
     */
    private String queryBooksByAuthor(String author) {
        StringBuilder sb = new StringBuilder();
        
        List<Book> books = bookMapper.selectList(
            new LambdaQueryWrapper<Book>()
                .like(Book::getAuthor, author)
                .eq(Book::getStatus, 1)
        );
        
        if (!books.isEmpty()) {
            sb.append("\n【").append(author).append("的书籍】\n");
            for (Book b : books) {
                sb.append(String.format("• 《%s》- %.2f元，%s\n",
                    b.getTitle(), b.getPrice(), getConditionDesc(b.getConditionLevel())));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 提取书名
     */
    private String extractBookTitle(String message) {
        // 匹配《书名》格式
        Pattern pattern = Pattern.compile("《(.+?)》");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // 匹配"有没有xxx"、"有xxx吗"格式
        Pattern pattern2 = Pattern.compile("有没有(.+?)(?:这本书|吗|？|\\?|$)");
        Matcher matcher2 = pattern2.matcher(message);
        if (matcher2.find()) {
            String title = matcher2.group(1).trim();
            if (title.length() >= 2 && title.length() <= 20) {
                return title;
            }
        }
        
        return null;
    }
    
    /**
     * 提取作者名
     */
    private String extractAuthor(String message) {
        Pattern pattern = Pattern.compile("(?:作者|作家)(?:是)?[：:]?\\s*(.+?)(?:的书|的作品|吗|？|\\?|$)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
    
    /**
     * 提取数字
     */
    private Integer extractNumber(String msg, String... suffixes) {
        for (String suffix : suffixes) {
            Pattern pattern = Pattern.compile("(\\d+)\\s*(?:元|块)?\\s*" + suffix);
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return null;
    }
    
    /**
     * 检查是否包含任意关键词
     */
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(String currentPage, String contextData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是\"二手书交易平台\"的智能客服助手，名叫\"小书\"。\n");
        prompt.append("你的职责是帮助用户了解平台功能、解答使用问题、推荐书籍、提供数据查询。\n");
        prompt.append("请用友好、专业的语气回答，回答要简洁明了，适当使用emoji表情。\n\n");
        
        prompt.append("以下是平台的知识库信息：\n");
        prompt.append(knowledgeBase);
        
        // 添加实时查询的数据
        if (contextData != null && !contextData.isEmpty()) {
            prompt.append("\n以下是根据用户问题从数据库实时查询的数据，请基于这些真实数据回答：\n");
            prompt.append(contextData);
        }
        
        // 添加页面上下文
        if (currentPage != null && !currentPage.isEmpty()) {
            prompt.append("\n【当前用户所在页面】\n");
            if (currentPage.contains("/book/") && !currentPage.contains("/my")) {
                prompt.append("用户正在查看书籍详情页，可以引导用户收藏、联系卖家或购买。\n");
            } else if (currentPage.equals("/home") || currentPage.equals("/")) {
                prompt.append("用户在首页浏览书籍列表，可以帮助用户搜索或推荐书籍。\n");
            } else if (currentPage.contains("/my/books")) {
                prompt.append("用户在\"我的书籍\"页面，可以帮助用户管理已发布的书籍。\n");
            } else if (currentPage.contains("/order")) {
                prompt.append("用户在订单相关页面，可以解答订单流程问题。\n");
            }
        }
        
        prompt.append("\n重要提示：\n");
        prompt.append("1. 回答要基于上面提供的真实数据，不要编造数据\n");
        prompt.append("2. 如果数据中没有相关信息，诚实告知用户\n");
        prompt.append("3. 不要透露用户的敏感信息（如密码、手机号、邮箱等）\n");
        prompt.append("4. 如果用户问的问题与平台无关，礼貌地引导回平台相关话题\n");
        
        return prompt.toString();
    }

    
    /**
     * 调用智谱AI API
     */
    private String callZhipuApi(String systemPrompt, String userMessage, List<AiChatDTO.ChatMessage> history) throws IOException {
        // 检查API Key
        if (apiKey == null || apiKey.isEmpty()) {
            return generateLocalResponse(userMessage);
        }
        
        // 构建消息列表
        JsonArray messages = new JsonArray();
        
        // 系统消息
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemPrompt);
        messages.add(systemMsg);
        
        // 历史消息
        for (AiChatDTO.ChatMessage msg : history) {
            JsonObject historyMsg = new JsonObject();
            historyMsg.addProperty("role", msg.getRole());
            historyMsg.addProperty("content", msg.getContent());
            messages.add(historyMsg);
        }
        
        // 当前用户消息
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);
        
        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 1024);
        
        // 发送请求
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json")))
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("智谱API调用失败: {}", response.code());
                return generateLocalResponse(userMessage);
            }
            
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }
    
    /**
     * 本地回复（API不可用时的降级方案，支持数据库查询）
     */
    private String generateLocalResponse(String message) {
        String msg = message.toLowerCase();
        StringBuilder response = new StringBuilder();
        
        // 优先处理价格范围查询
        String priceResult = queryBooksByPriceRange(message);
        if (!priceResult.isEmpty()) {
            return "📚 为您查询到以下书籍：" + priceResult;
        }
        
        // 查询统计数据
        if (containsAny(msg, "多少", "数量", "统计", "总共", "有几")) {
            try {
                Long totalUsers = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getStatus, 1));
                Long totalBooks = bookMapper.selectCount(new LambdaQueryWrapper<Book>().eq(Book::getStatus, 1));
                Long totalOrders = orderMapper.selectCount(null);
                Long completedOrders = orderMapper.selectCount(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, 5));
                
                response.append("📊 平台实时数据统计：\n\n");
                response.append("• 注册用户：").append(totalUsers).append(" 人\n");
                response.append("• 在售书籍：").append(totalBooks).append(" 本\n");
                response.append("• 订单总数：").append(totalOrders).append(" 笔\n");
                response.append("• 完成交易：").append(completedOrders).append(" 笔\n");
                return response.toString();
            } catch (Exception e) {
                log.warn("查询统计数据失败", e);
            }
        }
        
        // 查询分类
        if (containsAny(msg, "分类", "类别", "种类")) {
            try {
                List<Category> categories = categoryMapper.selectList(null);
                response.append("📚 平台书籍分类：\n\n");
                for (Category cat : categories) {
                    int count = bookMapper.countByCategoryId(cat.getId());
                    response.append("• ").append(cat.getName()).append("：").append(count).append(" 本\n");
                }
                return response.toString();
            } catch (Exception e) {
                log.warn("查询分类失败", e);
            }
        }
        
        // 推荐书籍
        if (containsAny(msg, "推荐", "有什么好书", "热门")) {
            try {
                List<Book> books = bookMapper.selectList(
                    new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .orderByDesc(Book::getViewCount)
                        .last("LIMIT 5")
                );
                response.append("🔥 热门推荐书籍：\n\n");
                for (int i = 0; i < books.size(); i++) {
                    Book b = books.get(i);
                    response.append(String.format("%d. 《%s》- %s\n   💰 %.2f元 | %s\n\n",
                        i + 1, b.getTitle(), b.getAuthor(), b.getPrice(), getConditionDesc(b.getConditionLevel())));
                }
                return response.toString();
            } catch (Exception e) {
                log.warn("查询推荐书籍失败", e);
            }
        }
        
        // 便宜书籍
        if (containsAny(msg, "便宜", "低价", "实惠")) {
            try {
                List<Book> books = bookMapper.selectList(
                    new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .orderByAsc(Book::getPrice)
                        .last("LIMIT 5")
                );
                response.append("💰 超值低价书籍：\n\n");
                for (Book b : books) {
                    response.append(String.format("• 《%s》仅 %.2f 元\n", b.getTitle(), b.getPrice()));
                }
                return response.toString();
            } catch (Exception e) {
                log.warn("查询低价书籍失败", e);
            }
        }
        
        // 查询特定书籍
        String bookTitle = extractBookTitle(message);
        if (bookTitle != null) {
            try {
                List<Book> books = bookMapper.selectList(
                    new LambdaQueryWrapper<Book>()
                        .like(Book::getTitle, bookTitle)
                        .eq(Book::getStatus, 1)
                );
                if (!books.isEmpty()) {
                    response.append("🔍 找到以下书籍：\n\n");
                    for (Book b : books) {
                        response.append(String.format("📖 《%s》\n", b.getTitle()));
                        response.append(String.format("   作者：%s\n", b.getAuthor()));
                        response.append(String.format("   价格：%.2f 元\n", b.getPrice()));
                        response.append(String.format("   成色：%s\n\n", getConditionDesc(b.getConditionLevel())));
                    }
                    return response.toString();
                } else {
                    return "😔 抱歉，暂时没有找到《" + bookTitle + "》这本书。\n\n你可以：\n• 换个关键词搜索\n• 浏览书籍列表看看其他好书";
                }
            } catch (Exception e) {
                log.warn("查询书籍失败", e);
            }
        }
        
        // 默认回复
        if (containsAny(msg, "发布", "卖书")) {
            return "发布书籍很简单！📚\n\n1. 点击顶部导航的「发布书籍」\n2. 填写书名、作者、价格等信息\n3. 上传书籍图片\n4. 选择分类和成色\n5. 点击发布即可\n\n💡 详细的描述和清晰的图片能让书更快卖出去哦！";
        } else if (containsAny(msg, "购买", "买书", "下单")) {
            return "购买书籍的步骤：📖\n\n1. 浏览或搜索想要的书籍\n2. 点击查看详情\n3. 确认后点击「立即购买」\n4. 填写收货地址\n5. 确认订单\n\n📦 卖家发货后记得及时确认收货哦！";
        } else if (containsAny(msg, "联系", "卖家", "聊天")) {
            return "联系卖家的方法：💬\n\n1. 进入书籍详情页\n2. 点击「联系卖家」按钮\n3. 在聊天窗口发送消息\n\n你也可以在「消息中心」查看所有对话记录。";
        } else if (containsAny(msg, "收费", "费用", "免费")) {
            return "平台目前完全免费！🎉\n\n• 发布书籍：免费\n• 浏览购买：免费\n• 消息沟通：免费\n\n我们致力于让闲置书籍流动起来！";
        } else if (containsAny(msg, "订单", "物流", "发货")) {
            return "订单流程说明：📦\n\n1. 买家下单 → 待确认\n2. 卖家确认 → 待支付\n3. 买家支付 → 待发货\n4. 卖家发货 → 待收货\n5. 买家确认 → 已完成\n\n如有问题可联系对方协商！";
        } else if (containsAny(msg, "你好", "在吗", "hi", "hello")) {
            return "你好！我是小书，二手书交易平台的智能助手 📚\n\n我可以帮你：\n• 查询平台数据统计\n• 推荐热门/低价书籍\n• 搜索特定书籍\n• 解答平台使用问题\n\n有什么可以帮助你的吗？";
        } else {
            return "我是小书，平台的智能助手 😊\n\n我可以帮你：\n• 查询平台统计数据（问我\"有多少书籍\"）\n• 推荐热门书籍（问我\"推荐好书\"）\n• 查找特定书籍（问我\"有没有《xxx》\"）\n• 解答平台使用问题\n\n请问有什么可以帮助你的？";
        }
    }
    
    private String getConditionDesc(Integer level) {
        return switch (level) {
            case 1 -> "全新";
            case 2 -> "九成新";
            case 3 -> "八成新";
            case 4 -> "七成新";
            case 5 -> "六成新及以下";
            default -> "未知";
        };
    }
}
