# 设计文档

## 概述

二手书交易管理系统采用前后端分离架构，后端使用Java SpringBoot 3.x + MyBatis-Plus，前端使用Vue3 + Vite + Naive UI。系统按照数据库→后端→前端的顺序开发，遵循RESTful API设计规范。

## 技术栈

### 后端技术栈
- **核心框架**: SpringBoot 3.x
- **ORM框架**: MyBatis-Plus 3.5+
- **数据库**: MySQL 8.0+
- **缓存**: Redis 7.0+（可选，用于会话和热点数据缓存）
- **认证**: JWT (JSON Web Token)
- **API文档**: Swagger3 (SpringDoc)
- **工具库**: Hutool、Lombok
- **密码加密**: BCrypt

### 前端技术栈
- **核心框架**: Vue3 + Vite
- **UI组件库**: Naive UI
- **状态管理**: Pinia
- **路由**: Vue Router 4.x
- **HTTP请求**: Axios
- **代码规范**: ESLint + Prettier

## 架构设计

### 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        前端 (Vue3)                           │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐        │
│  │  Views  │  │Components│  │  Store  │  │ Router  │        │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘        │
│       └────────────┴────────────┴────────────┘              │
│                          │                                   │
│                    ┌─────┴─────┐                            │
│                    │  Axios    │                            │
│                    └─────┬─────┘                            │
└──────────────────────────┼──────────────────────────────────┘
                           │ HTTP/REST API
┌──────────────────────────┼──────────────────────────────────┐
│                    后端 (SpringBoot)                         │
│                    ┌─────┴─────┐                            │
│                    │Controller │                            │
│                    └─────┬─────┘                            │
│                    ┌─────┴─────┐                            │
│                    │  Service  │                            │
│                    └─────┬─────┘                            │
│                    ┌─────┴─────┐                            │
│                    │  Mapper   │                            │
│                    └─────┬─────┘                            │
└──────────────────────────┼──────────────────────────────────┘
                           │
┌──────────────────────────┼──────────────────────────────────┐
│                    ┌─────┴─────┐                            │
│                    │  MySQL    │                            │
│                    └───────────┘                            │
└─────────────────────────────────────────────────────────────┘
```

### 后端分层架构

```
com.example.booktrading
├── config/              # 配置类
│   ├── SwaggerConfig    # API文档配置
│   ├── JwtConfig        # JWT配置
│   ├── WebMvcConfig     # Web配置（跨域等）
│   └── MyBatisPlusConfig# MyBatis-Plus配置
├── controller/          # 控制器层
│   ├── AuthController   # 认证接口
│   ├── UserController   # 用户接口
│   ├── BookController   # 书籍接口
│   ├── CategoryController# 分类接口
│   ├── OrderController  # 订单接口
│   ├── FavoriteController# 收藏接口
│   ├── ReviewController # 评价接口
│   ├── MessageController# 消息接口
│   └── AdminController  # 管理员接口
├── service/             # 业务逻辑层
│   └── impl/            # 业务实现类
├── mapper/              # 数据访问层
├── entity/              # 实体类
│   ├── po/              # 持久化对象（对应数据库表）
│   ├── dto/             # 数据传输对象（请求参数）
│   └── vo/              # 视图对象（响应数据）
├── utils/               # 工具类
│   ├── JwtUtil          # JWT工具
│   ├── PasswordUtil     # 密码工具
│   └── ResultUtil       # 响应封装工具
├── exception/           # 异常处理
│   ├── GlobalExceptionHandler # 全局异常处理器
│   └── BusinessException      # 业务异常类
├── constant/            # 常量类
│   ├── ResultCode       # 响应状态码
│   ├── BookStatus       # 书籍状态
│   └── OrderStatus      # 订单状态
└── interceptor/         # 拦截器
    └── JwtInterceptor   # JWT认证拦截器
```

## 组件与接口

### 统一响应格式

```java
public class Result<T> {
    private Integer code;      // 状态码
    private String msg;        // 提示信息
    private T data;            // 业务数据
    private Long timestamp;    // 时间戳
    private String requestId;  // 请求ID
}
```

### 状态码定义

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录/Token无效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

### API接口设计

#### 认证模块 `/api/auth`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /register | 用户注册 | 公开 |
| POST | /login | 用户登录 | 公开 |
| POST | /logout | 用户登出 | 登录 |

#### 用户模块 `/api/user`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /profile | 获取个人信息 | 登录 |
| PUT | /profile | 更新个人信息 | 登录 |
| PUT | /password | 修改密码 | 登录 |
| GET | /{id} | 获取用户公开信息 | 公开 |

#### 书籍模块 `/api/book`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /list | 书籍列表（分页、搜索、筛选） | 公开 |
| GET | /{id} | 书籍详情 | 公开 |
| POST | / | 发布书籍 | 登录 |
| PUT | /{id} | 编辑书籍 | 所有者 |
| DELETE | /{id} | 删除书籍 | 所有者 |
| PUT | /{id}/status | 更新书籍状态 | 所有者 |
| GET | /my | 我的书籍列表 | 登录 |

#### 分类模块 `/api/category`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /list | 分类列表 | 公开 |
| POST | / | 创建分类 | 管理员 |
| PUT | /{id} | 更新分类 | 管理员 |
| DELETE | /{id} | 删除分类 | 管理员 |

#### 收藏模块 `/api/favorite`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /list | 收藏列表 | 登录 |
| POST | /{bookId} | 添加收藏 | 登录 |
| DELETE | /{bookId} | 取消收藏 | 登录 |
| GET | /check/{bookId} | 检查收藏状态 | 登录 |

#### 订单模块 `/api/order`(支付功能直接跳过真实支付,点击即是支付成功)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /list | 订单列表 | 登录 |
| GET | /{id} | 订单详情 | 买家/卖家 |
| POST | / | 创建订单 | 登录 |
| PUT | /{id}/confirm | 卖家确认订单 | 卖家 |
| PUT | /{id}/pay | 买家支付 | 买家 |
| PUT | /{id}/ship | 卖家发货 | 卖家 |
| PUT | /{id}/receive | 买家确认收货 | 买家 |
| PUT | /{id}/cancel | 取消订单 | 买家/卖家 |

#### 评价模块 `/api/review`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | / | 提交评价 | 登录 |
| GET | /user/{userId} | 用户收到的评价列表 | 公开 |
| GET | /order/{orderId} | 订单的评价 | 买家/卖家 |

#### 消息模块 `/api/message`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /conversations | 会话列表 | 登录 |
| GET | /history/{userId} | 与某用户的聊天记录 | 登录 |
| POST | / | 发送消息 | 登录 |
| PUT | /read/{conversationId} | 标记已读 | 登录 |
| DELETE | /conversation/{userId} | 删除会话 | 登录 |
| GET | /unread-count | 未读消息数 | 登录 |

#### 管理员模块 `/api/admin`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /users | 用户列表 | 管理员 |
| PUT | /user/{id}/status | 启用/禁用用户 | 管理员 |
| GET | /statistics | 系统统计 | 管理员 |
| GET | /books | 书籍管理列表 | 管理员 |
| DELETE | /book/{id} | 删除书籍 | 管理员 |

## 数据模型

### 数据库ER图

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    User     │     │   Category  │     │    Book     │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id          │     │ id          │     │ id          │
│ username    │     │ name        │     │ title       │
│ password    │     │ sort        │     │ author      │
│ email       │     │ create_time │     │ price       │
│ phone       │     │ update_time │     │ condition   │
│ avatar      │     │ deleted     │     │ description │
│ role        │     └──────┬──────┘     │ images      │
│ status      │            │            │ status      │
│ avg_rating  │            │            │ user_id ────┼──┐
│ create_time │            └────────────┼─category_id │  │
│ update_time │                         │ create_time │  │
│ deleted     │                         │ update_time │  │
└──────┬──────┘                         │ deleted     │  │
       │                                └──────┬──────┘  │
       │                                       │         │
       │  ┌────────────────────────────────────┘         │
       │  │                                              │
       │  │  ┌─────────────┐                             │
       │  │  │  Favorite   │                             │
       │  │  ├─────────────┤                             │
       │  │  │ id          │                             │
       │  └──┼─book_id     │                             │
       └─────┼─user_id     │                             │
             │ create_time │                             │
             └─────────────┘                             │
                                                         │
┌─────────────┐     ┌─────────────┐     ┌─────────────┐ │
│   Order     │     │   Review    │     │   Message   │ │
├─────────────┤     ├─────────────┤     ├─────────────┤ │
│ id          │     │ id          │     │ id          │ │
│ order_no    │     │ order_id ───┼─────┤ sender_id ──┼─┤
│ book_id ────┼─────┤ reviewer_id │     │ receiver_id─┼─┘
│ buyer_id ───┼─┐   │ reviewee_id │     │ content     │
│ seller_id ──┼─┤   │ rating      │     │ is_read     │
│ price       │ │   │ content     │     │ create_time │
│ status      │ │   │ create_time │     │ deleted_by  │
│ create_time │ │   └─────────────┘     └─────────────┘
│ update_time │ │
│ deleted     │ │
└─────────────┘ │
                │
                └── 关联到 User 表
```

### 数据表设计

#### 用户表 (user)

```sql
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `avg_rating` DECIMAL(2,1) DEFAULT 0.0 COMMENT '平均评分',
  `rating_count` INT DEFAULT 0 COMMENT '评价数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  INDEX `idx_username` (`username`),
  INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 分类表 (category)

```sql
CREATE TABLE `category` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍分类表';
```

#### 书籍表 (book)

```sql
CREATE TABLE `book` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '书籍ID',
  `title` VARCHAR(200) NOT NULL COMMENT '书名',
  `author` VARCHAR(100) NOT NULL COMMENT '作者',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) COMMENT '原价',
  `condition` TINYINT NOT NULL COMMENT '成色：1-全新，2-九成新，3-八成新，4-七成新，5-六成新及以下',
  `description` TEXT COMMENT '描述',
  `images` VARCHAR(1000) COMMENT '图片URL（JSON数组）',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-在售，2-已预订，3-已售，4-下架',
  `user_id` BIGINT NOT NULL COMMENT '卖家ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`category_id`) REFERENCES `category`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍表';
```

#### 收藏表 (favorite)

```sql
CREATE TABLE `favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '书籍ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
  INDEX `idx_user_id` (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`book_id`) REFERENCES `book`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
```

#### 订单表 (order)

```sql
CREATE TABLE `order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
  `book_id` BIGINT NOT NULL COMMENT '书籍ID',
  `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `price` DECIMAL(10,2) NOT NULL COMMENT '成交价格',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-待确认，2-已确认，3-已支付，4-已发货，5-已完成，6-已取消',
  `shipping_address` VARCHAR(500) COMMENT '收货地址',
  `shipping_info` VARCHAR(200) COMMENT '物流信息',
  `cancel_reason` VARCHAR(200) COMMENT '取消原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_buyer_id` (`buyer_id`),
  INDEX `idx_seller_id` (`seller_id`),
  INDEX `idx_book_id` (`book_id`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`book_id`) REFERENCES `book`(`id`),
  FOREIGN KEY (`buyer_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`seller_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

#### 评价表 (review)

```sql
CREATE TABLE `review` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `reviewer_id` BIGINT NOT NULL COMMENT '评价者ID',
  `reviewee_id` BIGINT NOT NULL COMMENT '被评价者ID',
  `rating` TINYINT NOT NULL COMMENT '评分：1-5星',
  `content` VARCHAR(500) COMMENT '评价内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_order_reviewer` (`order_id`, `reviewer_id`),
  INDEX `idx_reviewee_id` (`reviewee_id`),
  FOREIGN KEY (`order_id`) REFERENCES `order`(`id`),
  FOREIGN KEY (`reviewer_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`reviewee_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';
```

#### 消息表 (message)

```sql
CREATE TABLE `message` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted_by_sender` TINYINT DEFAULT 0 COMMENT '发送者是否删除',
  `deleted_by_receiver` TINYINT DEFAULT 0 COMMENT '接收者是否删除',
  INDEX `idx_sender_id` (`sender_id`),
  INDEX `idx_receiver_id` (`receiver_id`),
  INDEX `idx_create_time` (`create_time`),
  FOREIGN KEY (`sender_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`receiver_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';
```



## 正确性属性

*正确性属性是指在系统所有有效执行中都应保持为真的特征或行为——本质上是关于系统应该做什么的形式化陈述。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*


基于需求分析，以下是系统的核心正确性属性：

### 属性1：用户注册唯一性

*对于任意*用户名或邮箱，如果该用户名或邮箱已存在于系统中，则使用相同用户名或邮箱的注册请求应被拒绝。

**验证需求：1.2**

### 属性2：JWT认证往返

*对于任意*有效用户，使用正确凭证登录获取的JWT令牌，应能成功访问受保护资源；使用无效或过期的令牌应返回401错误。

**验证需求：1.3, 1.5, 1.6**

### 属性3：密码修改往返

*对于任意*用户，使用正确旧密码修改密码后，应能使用新密码成功登录，旧密码应无法登录。

**验证需求：2.3, 2.4**

### 属性4：书籍所有权验证

*对于任意*书籍，只有书籍所有者才能编辑或删除该书籍，非所有者的编辑/删除请求应返回403错误。

**验证需求：3.3, 3.4, 3.5**

### 属性5：搜索结果正确性

*对于任意*搜索关键词，返回的所有书籍的书名或作者应包含该关键词；*对于任意*分类筛选，返回的所有书籍应属于指定分类；*对于任意*价格区间筛选，返回的所有书籍价格应在指定范围内。

**验证需求：4.2, 4.3, 4.4**

### 属性6：收藏状态一致性

*对于任意*用户和书籍，添加收藏后检查收藏状态应返回true，取消收藏后检查收藏状态应返回false；重复添加已收藏的书籍应被拒绝。

**验证需求：6.1, 6.2, 6.3, 6.5**

### 属性7：订单状态机正确性

*对于任意*订单，状态流转应遵循预定义的状态机：待确认→已确认→已支付→已发货→已完成，或在发货前可转为已取消；创建订单时书籍状态应变为已预订，完成订单时书籍状态应变为已售，取消订单时书籍状态应恢复为在售。

**验证需求：7.1, 7.4, 7.5, 7.6, 7.7, 7.8**

### 属性8：订单创建约束

*对于任意*订单创建请求，买家不能购买自己的书籍，且只能购买状态为"在售"的书籍。

**验证需求：7.2, 7.3**

### 属性9：评价约束与平均分计算

*对于任意*订单，只有状态为"已完成"时才能提交评价，且同一用户对同一订单只能评价一次；*对于任意*用户，其平均评分应等于所有收到评价的评分总和除以评价数量。

**验证需求：8.1, 8.3, 8.4, 8.7**

### 属性10：消息未读计数一致性

*对于任意*用户，未读消息计数应等于该用户收到的所有未读消息的数量；标记消息为已读后，未读计数应相应减少。

**验证需求：9.4, 9.5**

### 属性11：用户状态与登录能力

*对于任意*用户，禁用状态的用户应无法登录，启用后应能正常登录。

**验证需求：10.2, 10.3**

### 属性12：软删除一致性

*对于任意*被删除的用户、书籍或订单，数据应被标记为已删除（deleted=1）而非物理删除，查询时应默认排除已删除数据。

**验证需求：11.7**

### 属性13：密码加密存储

*对于任意*用户，存储在数据库中的密码应为BCrypt加密后的哈希值，而非明文密码。

**验证需求：11.1**

## 错误处理

### 全局异常处理

系统实现全局异常处理器，统一处理以下异常类型：

| 异常类型 | HTTP状态码 | 说明 |
|---------|-----------|------|
| BusinessException | 400 | 业务逻辑异常 |
| ValidationException | 400 | 参数校验异常 |
| UnauthorizedException | 401 | 未登录/Token无效 |
| ForbiddenException | 403 | 无权限访问 |
| NotFoundException | 404 | 资源不存在 |
| Exception | 500 | 系统内部错误 |

### 参数校验

使用JSR-380注解进行参数校验：

```java
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20个字符")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20个字符")
    private String password;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
```

## 测试策略

### 测试框架

- **后端单元测试**: JUnit 5 + Mockito
- **后端集成测试**: SpringBoot Test + H2内存数据库
- **属性测试**: jqwik（Java属性测试框架）
- **前端单元测试**: Vitest
- **E2E测试**: Playwright（可选）

### 测试分层

1. **单元测试**: 测试Service层业务逻辑，Mock Mapper层
2. **集成测试**: 测试Controller层接口，使用H2内存数据库
3. **属性测试**: 验证核心正确性属性，使用jqwik生成随机测试数据

### 属性测试配置

- 每个属性测试至少运行100次迭代
- 使用jqwik的@Property注解标记属性测试
- 每个测试需注释引用的设计文档属性编号

```java
// 示例：属性2 - JWT认证往返
@Property(tries = 100)
// Feature: used-book-trading-system, Property 2: JWT认证往返
void jwtAuthenticationRoundTrip(@ForAll @StringLength(min = 3, max = 20) String username) {
    // 1. 注册用户
    // 2. 登录获取Token
    // 3. 使用Token访问受保护资源
    // 4. 验证访问成功
}
```

### 测试覆盖要求

- 单元测试覆盖率 ≥ 80%
- 所有API接口需有集成测试
- 所有正确性属性需有属性测试
