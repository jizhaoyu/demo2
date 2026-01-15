package com.example.booktrading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booktrading.constant.BookStatus;
import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.dto.BookDTO;
import com.example.booktrading.entity.dto.BookQueryDTO;
import com.example.booktrading.entity.po.Book;
import com.example.booktrading.entity.po.Category;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.BookVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.BookMapper;
import com.example.booktrading.mapper.CategoryMapper;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.BookService;
import com.example.booktrading.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 书籍服务实现
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    
    @Override
    public Long create(BookDTO dto) {
        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        book.setUserId(UserContext.getUserId());
        book.setStatus(BookStatus.ON_SALE.getCode());
        book.setViewCount(0);
        
        bookMapper.insert(book);
        return book.getId();
    }
    
    @Override
    public void update(Long id, BookDTO dto) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        
        // 验证所有权
        if (!book.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(ResultCode.BOOK_OWNER_ONLY);
        }
        
        BeanUtils.copyProperties(dto, book);
        bookMapper.updateById(book);
    }
    
    @Override
    public void delete(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        
        // 验证所有权（管理员可删除任意书籍）
        if (!book.getUserId().equals(UserContext.getUserId()) && !UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.BOOK_OWNER_ONLY);
        }
        
        bookMapper.deleteById(id);
    }
    
    @Override
    public void updateStatus(Long id, Integer status) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        
        // 验证所有权
        if (!book.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(ResultCode.BOOK_OWNER_ONLY);
        }
        
        book.setStatus(status);
        bookMapper.updateById(book);
    }
    
    @Override
    public IPage<BookVO> list(BookQueryDTO query) {
        Page<Book> page = new Page<>(query.getPage(), query.getSize());
        
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        // 只查询在售书籍
        wrapper.eq(Book::getStatus, BookStatus.ON_SALE.getCode());
        
        // 关键词搜索
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(Book::getTitle, query.getKeyword())
                    .or().like(Book::getAuthor, query.getKeyword()));
        }
        
        // 分类筛选
        if (query.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, query.getCategoryId());
        }
        
        // 价格筛选
        if (query.getMinPrice() != null) {
            wrapper.ge(Book::getPrice, query.getMinPrice());
        }
        if (query.getMaxPrice() != null) {
            wrapper.le(Book::getPrice, query.getMaxPrice());
        }
        
        // 排序
        if ("price".equals(query.getSortBy())) {
            if ("asc".equals(query.getSortOrder())) {
                wrapper.orderByAsc(Book::getPrice);
            } else {
                wrapper.orderByDesc(Book::getPrice);
            }
        } else if ("viewCount".equals(query.getSortBy())) {
            // 按浏览量排序（热门）
            if ("asc".equals(query.getSortOrder())) {
                wrapper.orderByAsc(Book::getViewCount);
            } else {
                wrapper.orderByDesc(Book::getViewCount);
            }
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(Book::getCreateTime);
        }
        
        IPage<Book> bookPage = bookMapper.selectPage(page, wrapper);
        return bookPage.convert(this::convertToVO);
    }
    
    @Override
    public BookVO getById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        
        // 增加浏览次数
        book.setViewCount(book.getViewCount() + 1);
        bookMapper.updateById(book);
        
        return convertToVO(book);
    }
    
    @Override
    public IPage<BookVO> myBooks(Integer page, Integer size) {
        Long currentUserId = UserContext.getUserId();
        System.out.println("========== myBooks Debug ==========");
        System.out.println("Current User ID from UserContext: " + currentUserId);
        
        Page<Book> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getUserId, currentUserId);
        wrapper.orderByDesc(Book::getCreateTime);
        
        IPage<Book> bookPage = bookMapper.selectPage(pageParam, wrapper);
        System.out.println("Query result - Total: " + bookPage.getTotal() + ", Records: " + bookPage.getRecords().size());
        System.out.println("====================================");
        
        return bookPage.convert(this::convertToVO);
    }
    
    /**
     * 转换为VO
     */
    private BookVO convertToVO(Book book) {
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);
        
        // 设置成色描述
        vo.setConditionDesc(getConditionDesc(book.getConditionLevel()));
        
        // 设置状态描述
        BookStatus status = BookStatus.fromCode(book.getStatus());
        vo.setStatusDesc(status != null ? status.getDesc() : "未知");
        
        // 获取分类信息
        Category category = categoryMapper.selectById(book.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        // 获取卖家信息
        User seller = userMapper.selectById(book.getUserId());
        if (seller != null) {
            vo.setSellerId(seller.getId());
            vo.setSellerName(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
            vo.setSellerRating(seller.getAvgRating());
            vo.setSellerRatingCount(seller.getRatingCount());
        }
        
        return vo;
    }
    
    /**
     * 获取成色描述
     */
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
