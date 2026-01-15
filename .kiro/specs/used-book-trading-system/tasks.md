# 实现计划：二手书交易管理系统

## 概述

按照数据库→后端→前端的顺序开发，确保每个阶段完成后可独立验证。后端使用Java SpringBoot 3.x + MyBatis-Plus，前端使用Vue3 + Vite + Naive UI。

## 任务列表

### 阶段一：数据库设计与初始化

- [x] 1. 创建数据库和初始化脚本
  - [x] 1.1 创建MySQL数据库和用户表(user)
    - 包含id、username、password、email、phone、avatar、role、status、avg_rating、rating_count等字段
    - 添加索引和约束
    - _需求: 1.1, 11.1_
  - [x] 1.2 创建分类表(category)和书籍表(book)
    - category包含id、name、sort等字段
    - book包含id、title、author、price、condition、description、images、status、user_id、category_id等字段
    - 添加外键关系和索引
    - _需求: 3.1, 5.1, 11.2_
  - [x] 1.3 创建收藏表(favorite)和订单表(order)
    - favorite包含id、user_id、book_id等字段
    - order包含id、order_no、book_id、buyer_id、seller_id、price、status等字段
    - 添加外键关系和索引
    - _需求: 6.1, 7.1, 11.3_
  - [x] 1.4 创建评价表(review)和消息表(message)
    - review包含id、order_id、reviewer_id、reviewee_id、rating、content等字段
    - message包含id、sender_id、receiver_id、content、is_read等字段
    - 添加外键关系和索引
    - _需求: 8.1, 9.1, 11.4, 11.5_
  - [x] 1.5 创建初始化数据脚本
    - 插入预定义分类数据（文学、教材、科技、艺术、生活、其他）
    - 插入管理员账户
    - _需求: 5.1_

- [x] 2. 检查点 - 数据库验证
  - 确保所有表创建成功，外键关系正确
  - 确保初始数据插入成功

### 阶段二：后端基础架构

- [x] 3. 初始化SpringBoot项目结构
  - [x] 3.1 配置pom.xml依赖
    - 添加SpringBoot、MyBatis-Plus、MySQL、JWT、Swagger、Hutool、Lombok等依赖
    - _需求: 11.1_
  - [x] 3.2 创建application.yml配置文件
    - 配置数据库连接、JWT密钥、Swagger、MyBatis-Plus等
    - _需求: 11.6_
  - [x] 3.3 创建项目包结构
    - 创建config、controller、service、mapper、entity、utils、exception、constant、interceptor包
    - _需求: 11.8_

- [x] 4. 实现通用组件
  - [x] 4.1 创建统一响应类Result和状态码常量ResultCode
    - 实现code、msg、data、timestamp、requestId字段
    - 定义200/400/401/403/404/500状态码
    - _需求: 11.8_
  - [x] 4.2 创建全局异常处理器GlobalExceptionHandler
    - 处理BusinessException、ValidationException、UnauthorizedException等
    - _需求: 11.8_
  - [x] 4.3 创建JWT工具类JwtUtil
    - 实现Token生成、解析、验证功能
    - _需求: 1.3, 1.5_
  - [x] 4.4 创建密码工具类PasswordUtil
    - 使用BCrypt实现密码加密和验证
    - _需求: 11.1_
  - [x] 4.5 创建JWT拦截器JwtInterceptor
    - 实现Token验证和用户信息注入
    - _需求: 1.5, 1.6_
  - [x] 4.6 配置WebMvc（跨域、拦截器）
    - 配置CORS跨域
    - 注册JWT拦截器，排除公开接口
    - _需求: 1.5_
  - [x] 4.7 配置Swagger文档
    - 配置SpringDoc OpenAPI
    - _需求: 11.8_
  - [x] 4.8 配置MyBatis-Plus
    - 配置分页插件、逻辑删除、自动填充
    - _需求: 11.7_

- [x] 5. 检查点 - 后端基础架构验证
  - 确保项目能正常启动
  - 确保Swagger文档可访问

### 阶段三：后端实体与数据访问层

- [x] 6. 创建实体类和Mapper
  - [x] 6.1 创建User实体类和UserMapper
    - PO类对应user表，DTO类用于请求参数，VO类用于响应
    - _需求: 1.1, 2.1_
  - [x] 6.2 创建Category实体类和CategoryMapper
    - _需求: 5.1_
  - [x] 6.3 创建Book实体类和BookMapper
    - _需求: 3.1, 4.1_
  - [x] 6.4 创建Favorite实体类和FavoriteMapper
    - _需求: 6.1_
  - [x] 6.5 创建Order实体类和OrderMapper
    - 定义订单状态常量
    - _需求: 7.1_
  - [x] 6.6 创建Review实体类和ReviewMapper
    - _需求: 8.1_
  - [x] 6.7 创建Message实体类和MessageMapper
    - _需求: 9.1_

### 阶段四：后端业务逻辑层

- [x] 7. 实现认证模块
  - [x] 7.1 实现AuthService（注册、登录、登出）
    - 注册时验证用户名/邮箱唯一性，密码BCrypt加密
    - 登录时验证凭证，生成JWT Token
    - _需求: 1.1, 1.2, 1.3, 1.4, 1.7_
  - [x] 7.2 实现AuthController
    - POST /api/auth/register
    - POST /api/auth/login
    - POST /api/auth/logout
    - _需求: 1.1-1.7_
  - [ ]* 7.3 编写属性测试：用户注册唯一性
    - **属性1: 用户注册唯一性**
    - **验证需求: 1.2**
  - [ ]* 7.4 编写属性测试：JWT认证往返
    - **属性2: JWT认证往返**
    - **验证需求: 1.3, 1.5, 1.6**

- [x] 8. 实现用户模块
  - [x] 8.1 实现UserService（获取/更新个人信息、修改密码）
    - _需求: 2.1, 2.2, 2.3, 2.4_
  - [x] 8.2 实现UserController
    - GET /api/user/profile
    - PUT /api/user/profile
    - PUT /api/user/password
    - GET /api/user/{id}
    - _需求: 2.1-2.4_
  - [ ]* 8.3 编写属性测试：密码修改往返
    - **属性3: 密码修改往返**
    - **验证需求: 2.3, 2.4**

- [x] 9. 实现分类模块
  - [x] 9.1 实现CategoryService（CRUD）
    - 删除时检查是否有关联书籍
    - _需求: 5.1-5.5_
  - [x] 9.2 实现CategoryController
    - GET /api/category/list
    - POST /api/category（管理员）
    - PUT /api/category/{id}（管理员）
    - DELETE /api/category/{id}（管理员）
    - _需求: 5.1-5.5_

- [x] 10. 实现书籍模块
  - [x] 10.1 实现BookService（发布、编辑、删除、状态更新、列表、详情、搜索）
    - 编辑/删除时验证所有权
    - 支持关键词搜索、分类筛选、价格筛选、排序
    - _需求: 3.1-3.7, 4.1-4.6_
  - [x] 10.2 实现BookController
    - GET /api/book/list
    - GET /api/book/{id}
    - POST /api/book
    - PUT /api/book/{id}
    - DELETE /api/book/{id}
    - PUT /api/book/{id}/status
    - GET /api/book/my
    - _需求: 3.1-3.7, 4.1-4.6_
  - [ ]* 10.3 编写属性测试：书籍所有权验证
    - **属性4: 书籍所有权验证**
    - **验证需求: 3.3, 3.4, 3.5**
  - [ ]* 10.4 编写属性测试：搜索结果正确性
    - **属性5: 搜索结果正确性**
    - **验证需求: 4.2, 4.3, 4.4**

- [x] 11. 检查点 - 核心模块验证
  - 确保认证、用户、分类、书籍模块接口正常工作
  - 通过Swagger测试各接口

- [x] 12. 实现收藏模块
  - [x] 12.1 实现FavoriteService（添加、取消、列表、检查状态）
    - 添加时检查是否已收藏
    - _需求: 6.1-6.6_
  - [x] 12.2 实现FavoriteController
    - GET /api/favorite/list
    - POST /api/favorite/{bookId}
    - DELETE /api/favorite/{bookId}
    - GET /api/favorite/check/{bookId}
    - _需求: 6.1-6.6_
  - [ ]* 12.3 编写属性测试：收藏状态一致性
    - **属性6: 收藏状态一致性**
    - **验证需求: 6.1, 6.2, 6.3, 6.5**

- [x] 13. 实现订单模块
  - [x] 13.1 实现OrderService（创建、确认、支付、发货、收货、取消、列表）
    - 创建时验证不能购买自己的书、只能购买在售书籍
    - 状态流转时更新书籍状态
    - _需求: 7.1-7.9_
  - [x] 13.2 实现OrderController
    - GET /api/order/list
    - GET /api/order/{id}
    - POST /api/order
    - PUT /api/order/{id}/confirm
    - PUT /api/order/{id}/pay
    - PUT /api/order/{id}/ship
    - PUT /api/order/{id}/receive
    - PUT /api/order/{id}/cancel
    - _需求: 7.1-7.9_
  - [ ]* 13.3 编写属性测试：订单状态机正确性
    - **属性7: 订单状态机正确性**
    - **验证需求: 7.1, 7.4, 7.5, 7.6, 7.7, 7.8**
  - [ ]* 13.4 编写属性测试：订单创建约束
    - **属性8: 订单创建约束**
    - **验证需求: 7.2, 7.3**

- [x] 14. 实现评价模块
  - [x] 14.1 实现ReviewService（提交评价、获取评价列表、更新用户平均分）
    - 提交时验证订单状态为已完成、未重复评价
    - 提交后更新被评价用户的平均分
    - _需求: 8.1-8.7_
  - [x] 14.2 实现ReviewController
    - POST /api/review
    - GET /api/review/user/{userId}
    - GET /api/review/order/{orderId}
    - _需求: 8.1-8.7_
  - [ ]* 14.3 编写属性测试：评价约束与平均分计算
    - **属性9: 评价约束与平均分计算**
    - **验证需求: 8.1, 8.3, 8.4, 8.7**

- [x] 15. 实现消息模块
  - [x] 15.1 实现MessageService（发送、会话列表、聊天记录、标记已读、删除会话、未读计数）
    - 发送时检查用户状态
    - _需求: 9.1-9.7_
  - [x] 15.2 实现MessageController
    - GET /api/message/conversations
    - GET /api/message/history/{userId}
    - POST /api/message
    - PUT /api/message/read/{conversationId}
    - DELETE /api/message/conversation/{userId}
    - GET /api/message/unread-count
    - _需求: 9.1-9.7_
  - [ ]* 15.3 编写属性测试：消息未读计数一致性
    - **属性10: 消息未读计数一致性**
    - **验证需求: 9.4, 9.5**

- [x] 16. 实现管理员模块
  - [x] 16.1 实现AdminService（用户列表、启用/禁用用户、统计数据）
    - _需求: 10.1-10.5_
  - [x] 16.2 实现AdminController
    - GET /api/admin/users
    - PUT /api/admin/user/{id}/status
    - GET /api/admin/statistics
    - GET /api/admin/books
    - DELETE /api/admin/book/{id}
    - _需求: 10.1-10.5_
  - [ ]* 16.3 编写属性测试：用户状态与登录能力
    - **属性11: 用户状态与登录能力**
    - **验证需求: 10.2, 10.3**

- [x] 17. 检查点 - 后端完整验证
  - 确保所有接口正常工作
  - 确保所有属性测试通过
  - 通过Swagger完整测试业务流程

### 阶段五：前端基础架构

- [x] 18. 初始化Vue3项目
  - [x] 18.1 使用Vite创建Vue3项目
    - 配置TypeScript支持
    - _需求: 无_
  - [x] 18.2 安装和配置依赖
    - 安装Naive UI、Pinia、Vue Router、Axios
    - 配置ESLint和Prettier
    - _需求: 无_
  - [x] 18.3 创建项目目录结构
    - 创建views、components、store、service、utils、router目录
    - _需求: 无_
  - [x] 18.4 配置Vite（代理、环境变量）
    - 配置开发环境API代理
    - 配置环境变量文件
    - _需求: 无_

- [x] 19. 实现前端通用组件
  - [x] 19.1 封装Axios请求工具
    - 配置请求/响应拦截器
    - 统一处理Token、错误提示、加载态
    - _需求: 1.5, 1.6_
  - [x] 19.2 配置Vue Router和路由守卫
    - 定义路由规则
    - 实现登录拦截
    - _需求: 1.5, 1.6_
  - [x] 19.3 配置Pinia状态管理
    - 创建userStore（用户信息、Token）
    - _需求: 1.3_
  - [x] 19.4 创建布局组件
    - 创建主布局（头部导航、侧边栏、内容区）
    - _需求: 无_

- [x] 20. 检查点 - 前端基础架构验证
  - 确保项目能正常启动
  - 确保路由和状态管理正常工作

### 阶段六：前端页面开发

- [x] 21. 实现认证页面
  - [x] 21.1 实现登录页面
    - 表单验证、登录请求、Token存储
    - _需求: 1.3, 1.4_
  - [x] 21.2 实现注册页面
    - 表单验证、注册请求
    - _需求: 1.1, 1.2_

- [-] 22. 实现书籍相关页面
  - [x] 22.1 实现书籍列表页面
    - 分页、搜索、分类筛选、价格筛选、排序
    - _需求: 4.1-4.6_
  - [x] 22.2 实现书籍详情页面
    - 显示书籍信息、卖家信息、评分
    - 收藏按钮、购买按钮
    - _需求: 4.5, 6.5, 8.6_
  - [x] 22.3 实现发布/编辑书籍页面
    - 表单验证、图片上传
    - _需求: 3.1, 3.2, 3.3, 3.7_
  - [x] 22.4 实现我的书籍页面
    - 书籍列表、状态管理、编辑、删除
    - _需求: 3.3, 3.5, 3.6_

- [x] 23. 实现用户相关页面
  - [x] 23.1 实现个人中心页面
    - 个人信息展示和编辑
    - 修改密码
    - _需求: 2.1, 2.2, 2.3, 2.4_
  - [x] 23.2 实现我的收藏页面
    - 收藏列表、取消收藏
    - _需求: 6.3, 6.4_
  - [x] 23.3 实现用户主页
    - 显示用户公开信息、评价列表
    - _需求: 8.5_

- [x] 24. 实现订单相关页面
  - [x] 24.1 实现订单列表页面
    - 买家订单、卖家订单切换
    - 订单状态筛选
    - _需求: 7.9_
  - [x] 24.2 实现订单详情页面
    - 订单信息、状态操作按钮
    - 评价入口
    - _需求: 7.4-7.8, 8.1_
  - [x] 24.3 实现创建订单页面
    - 确认购买信息、填写收货地址
    - _需求: 7.1_

- [x] 25. 实现消息相关页面
  - [x] 25.1 实现消息中心页面
    - 会话列表、未读标记
    - _需求: 9.2, 9.4_
  - [x] 25.2 实现聊天页面
    - 聊天记录、发送消息
    - _需求: 9.1, 9.3, 9.5_

- [x] 26. 实现管理员页面
  - [x] 26.1 实现用户管理页面
    - 用户列表、启用/禁用
    - _需求: 10.1, 10.2, 10.3_
  - [x] 26.2 实现分类管理页面
    - 分类CRUD
    - _需求: 5.2, 5.3, 5.4, 5.5_
  - [x] 26.3 实现数据统计页面
    - 显示系统统计数据
    - _需求: 10.5_

- [x] 27. 检查点 - 前端完整验证
  - 确保所有页面正常显示
  - 确保前后端联调正常

### 阶段七：集成测试与优化

- [ ] 28. 集成测试
  - [ ] 28.1 完整业务流程测试
    - 注册→登录→发布书籍→搜索→购买→评价
    - _需求: 全部_
  - [ ]* 28.2 编写属性测试：软删除一致性
    - **属性12: 软删除一致性**
    - **验证需求: 11.7**
  - [ ]* 28.3 编写属性测试：密码加密存储
    - **属性13: 密码加密存储**
    - **验证需求: 11.1**

- [ ] 29. 最终检查点
  - 确保所有功能正常
  - 确保所有测试通过
  - 准备部署文档

## 备注

- 标记 `*` 的任务为可选任务（属性测试），可根据时间安排选择性实现
- 每个检查点需确保当前阶段功能完整可用后再进入下一阶段
- 属性测试使用jqwik框架，每个测试至少运行100次迭代
