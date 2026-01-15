# 二手书交易管理系统 - 后端

基于 SpringBoot 3.x + MyBatis-Plus 的后端服务。

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

## 快速启动

### 1. 数据库初始化

```bash
# 登录MySQL并执行脚本
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p book_trading < src/main/resources/sql/data.sql
```

### 2. 配置数据库连接

修改 `src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/book_trading
    username: your_username
    password: your_password
```

### 3. 启动服务

```bash
# 开发环境
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/book-trading-1.0.0.jar
```

## 接口文档

启动后访问: http://localhost:8080/api/doc.html

## 项目配置

### 主要配置项 (application.yml)

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| server.port | 服务端口 | 8080 |
| app.jwt.secret | JWT密钥 | - |
| app.jwt.expiration | Token有效期(ms) | 28800000 (8小时) |
| app.upload.path | 文件上传路径 | ./uploads |

### 环境配置

- `application-dev.yml`: 开发环境
- `application-prod.yml`: 生产环境
- `application-test.yml`: 测试环境

## 数据库设计

### 数据表

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| category | 分类表 |
| book | 书籍表 |
| favorite | 收藏表 |
| orders | 订单表 |
| review | 评价表 |
| message | 消息表 |

### ER关系

- user 1:N book (用户发布书籍)
- user 1:N orders (买家/卖家)
- book N:1 category (书籍分类)
- user N:N book (收藏关系)
- orders 1:N review (订单评价)
- user N:N user (消息关系)

## API接口

### 认证模块 `/api/auth`
- POST `/register` - 用户注册
- POST `/login` - 用户登录
- POST `/logout` - 用户登出

### 用户模块 `/api/user`
- GET `/profile` - 获取个人信息
- PUT `/profile` - 更新个人信息
- PUT `/password` - 修改密码
- GET `/{id}` - 获取用户公开信息

### 书籍模块 `/api/book`
- GET `/list` - 书籍列表
- GET `/{id}` - 书籍详情
- POST `/` - 发布书籍
- PUT `/{id}` - 编辑书籍
- DELETE `/{id}` - 删除书籍
- PUT `/{id}/status` - 更新状态
- GET `/my` - 我的书籍

### 分类模块 `/api/category`
- GET `/list` - 分类列表
- POST `/` - 创建分类 (管理员)
- PUT `/{id}` - 更新分类 (管理员)
- DELETE `/{id}` - 删除分类 (管理员)

### 收藏模块 `/api/favorite`
- GET `/list` - 收藏列表
- POST `/{bookId}` - 添加收藏
- DELETE `/{bookId}` - 取消收藏
- GET `/check/{bookId}` - 检查收藏状态

### 订单模块 `/api/order`
- GET `/list` - 订单列表
- GET `/{id}` - 订单详情
- POST `/` - 创建订单
- PUT `/{id}/confirm` - 确认订单
- PUT `/{id}/pay` - 支付订单
- PUT `/{id}/ship` - 发货
- PUT `/{id}/receive` - 确认收货
- PUT `/{id}/cancel` - 取消订单

### 评价模块 `/api/review`
- POST `/` - 提交评价
- GET `/user/{userId}` - 用户评价列表
- GET `/order/{orderId}` - 订单评价

### 消息模块 `/api/message`
- GET `/conversations` - 会话列表
- GET `/history/{userId}` - 聊天记录
- POST `/` - 发送消息
- PUT `/read/{conversationId}` - 标记已读
- DELETE `/conversation/{userId}` - 删除会话
- GET `/unread-count` - 未读数量

### 管理模块 `/api/admin`
- GET `/users` - 用户列表
- PUT `/user/{id}/status` - 启用/禁用用户
- GET `/statistics` - 系统统计
- GET `/books` - 书籍管理列表
- DELETE `/book/{id}` - 删除书籍

## 统一响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {},
  "timestamp": 1704067200000,
  "requestId": "req-xxx"
}
```

### 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录/Token无效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

## 测试

```bash
# 运行测试
mvn test

# 跳过测试打包
mvn clean package -DskipTests
```
