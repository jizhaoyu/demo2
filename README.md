# 二手书交易管理系统

一个基于 SpringBoot 3.x + Vue3 的前后端分离二手书交易平台。

## 项目概述

本系统提供二手书籍的发布、浏览、购买和管理功能，支持用户注册登录、书籍信息管理、交易流程管理、站内消息等核心功能。

### 主要功能

- **用户管理**: 注册、登录、个人信息管理、密码修改
- **书籍管理**: 发布、编辑、删除、状态管理、图片上传
- **书籍浏览**: 列表展示、关键词搜索、分类筛选、价格筛选、排序
- **收藏功能**: 添加/取消收藏、收藏列表
- **订单管理**: 创建订单、确认、支付、发货、收货、取消
- **交易评价**: 买卖双方互评、评分统计
- **站内消息**: 用户间私信、会话管理、未读提醒
- **管理后台**: 用户管理、分类管理、数据统计

## 技术栈

### 后端
- **核心框架**: SpringBoot 3.2.0
- **ORM框架**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0+
- **认证**: JWT (jjwt 0.12.3)
- **API文档**: SpringDoc OpenAPI 2.3.0
- **工具库**: Hutool 5.8.23、Lombok

### 前端
- **核心框架**: Vue 3.4.0 + Vite 5.0
- **UI组件库**: Naive UI 2.36.0
- **状态管理**: Pinia 2.1.7
- **路由**: Vue Router 4.2.5
- **HTTP请求**: Axios 1.6.2
- **语言**: TypeScript 5.2

## 环境要求

### 后端环境
- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 前端环境
- Node.js 18.x+
- npm 9.x+ 或 yarn 1.22+

## 快速开始

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行建表脚本
source backgrond/src/main/resources/sql/schema.sql

# 执行初始化数据脚本
source backgrond/src/main/resources/sql/data.sql
```

### 2. 后端启动

```bash
# 进入后端目录
cd backgrond

# 修改数据库配置 (application-dev.yml)
# 配置数据库用户名和密码

# 安装依赖并启动
mvn spring-boot:run
```

后端启动后访问:
- API服务: http://localhost:8080/api
- Swagger文档: http://localhost:8080/api/doc.html

### 3. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问: http://localhost:3000

## 项目结构


### 后端结构
```
backgrond/
├── src/main/java/com/example/booktrading/
│   ├── config/              # 配置类
│   │   ├── MyBatisPlusConfig.java
│   │   ├── SwaggerConfig.java
│   │   └── WebMvcConfig.java
│   ├── controller/          # 控制器层
│   │   ├── AdminController.java
│   │   ├── AuthController.java
│   │   ├── BookController.java
│   │   ├── CategoryController.java
│   │   ├── FavoriteController.java
│   │   ├── MessageController.java
│   │   ├── OrderController.java
│   │   ├── ReviewController.java
│   │   └── UserController.java
│   ├── service/             # 业务逻辑层
│   │   └── impl/            # 业务实现类
│   ├── mapper/              # 数据访问层
│   ├── entity/              # 实体类
│   │   ├── po/              # 持久化对象
│   │   ├── dto/             # 数据传输对象
│   │   └── vo/              # 视图对象
│   ├── utils/               # 工具类
│   ├── exception/           # 异常处理
│   ├── constant/            # 常量类
│   └── interceptor/         # 拦截器
├── src/main/resources/
│   ├── application.yml      # 主配置
│   ├── application-dev.yml  # 开发环境配置
│   ├── application-prod.yml # 生产环境配置
│   └── sql/                 # 数据库脚本
│       ├── schema.sql       # 建表脚本
│       └── data.sql         # 初始化数据
└── pom.xml
```

### 前端结构
```
frontend/
├── src/
│   ├── assets/              # 静态资源
│   ├── components/          # 公共组件
│   ├── layouts/             # 布局组件
│   │   ├── MainLayout.vue   # 主布局
│   │   └── AdminLayout.vue  # 管理后台布局
│   ├── router/              # 路由配置
│   ├── service/             # API接口封装
│   ├── store/               # Pinia状态管理
│   ├── types/               # TypeScript类型定义
│   ├── utils/               # 工具函数
│   ├── views/               # 页面组件
│   │   ├── auth/            # 认证页面
│   │   ├── book/            # 书籍页面
│   │   ├── user/            # 用户页面
│   │   ├── order/           # 订单页面
│   │   ├── message/         # 消息页面
│   │   ├── admin/           # 管理后台页面
│   │   └── error/           # 错误页面
│   ├── App.vue
│   └── main.ts
├── .env.development         # 开发环境变量
├── .env.production          # 生产环境变量
├── vite.config.ts           # Vite配置
├── tsconfig.json            # TypeScript配置
└── package.json
```

## API接口文档

启动后端后访问 Swagger 文档: http://localhost:8080/api/doc.html

### 主要接口模块

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 认证 | /api/auth | 登录、注册、登出 |
| 用户 | /api/user | 个人信息管理 |
| 书籍 | /api/book | 书籍CRUD、搜索 |
| 分类 | /api/category | 分类管理 |
| 收藏 | /api/favorite | 收藏管理 |
| 订单 | /api/order | 订单管理 |
| 评价 | /api/review | 评价管理 |
| 消息 | /api/message | 站内消息 |
| 管理 | /api/admin | 管理后台 |

## 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 拥有所有权限 |
| 普通用户 | testuser1 | test123 | 测试用户1 |
| 普通用户 | testuser2 | test123 | 测试用户2 |

> 注意: 初始密码需要在 data.sql 中使用正确的 BCrypt 加密值

## 配置说明

### 后端配置 (application-dev.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/book_trading
    username: your_username  # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码
```

### 前端配置 (.env.development)

```bash
VITE_API_BASE_URL=/api  # API基础路径
```

## 生产部署

### 后端打包

```bash
cd backgrond
mvn clean package -DskipTests
# 生成 target/book-trading-1.0.0.jar
```

### 前端打包

```bash
cd frontend
npm run build
# 生成 dist/ 目录
```

### 部署方式

1. **后端**: 使用 `java -jar book-trading-1.0.0.jar --spring.profiles.active=prod` 启动
2. **前端**: 将 `dist/` 目录部署到 Nginx 或其他静态服务器
3. **Nginx配置**: 配置反向代理将 `/api` 请求转发到后端服务

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /path/to/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # API反向代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 常见问题

### Q1: 数据库连接失败
- 检查 MySQL 服务是否启动
- 检查数据库用户名密码是否正确
- 检查数据库 `book_trading` 是否已创建

### Q2: 前端无法访问后端API
- 检查后端服务是否启动
- 检查 Vite 代理配置是否正确
- 检查端口是否被占用

### Q3: JWT Token 过期
- 默认 Token 有效期为 8 小时
- 可在 `application.yml` 中修改 `app.jwt.expiration`

## 开发说明

### 代码规范
- 后端遵循 Alibaba Java 开发手册
- 前端使用 ESLint + Prettier 进行代码格式化

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关

## License

MIT License
