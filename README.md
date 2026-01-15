# 二手书交易管理系统

一个基于 SpringBoot 3.x + Vue3 的前后端分离二手书交易平台。

## 项目概述

本系统提供二手书籍的发布、浏览、购买和管理功能，支持用户注册登录、书籍信息管理、交易流程管理、站内消息等核心功能。

### 主要功能

- **用户管理**: 注册、登录、个人信息管理、密码修改、用户评分
- **书籍管理**: 发布、编辑、删除、状态管理、图片上传、成色标注
- **书籍浏览**: 列表展示、关键词搜索、分类筛选、价格筛选、排序、浏览统计
- **收藏功能**: 添加/取消收藏、收藏列表查看
- **订单管理**: 创建订单、确认、支付、发货、收货、取消、订单追踪
- **交易评价**: 买卖双方互评、评分统计、信誉体系
- **站内消息**: 用户间私信、会话管理、未读提醒、消息删除
- **管理后台**: 用户管理、分类管理、数据统计、权限控制
- **AI 智能助手**: 智能客服、书籍推荐、平台指南、实时数据查询（集成智谱 GLM-4）

## 技术栈

### 后端
- **核心框架**: SpringBoot 3.2.0
- **ORM框架**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0+
- **认证**: JWT (jjwt 0.12.3)
- **API文档**: SpringDoc OpenAPI 2.3.0
- **工具库**: Hutool 5.8.23、Lombok
- **HTTP客户端**: OkHttp 4.12.0
- **JSON处理**: Gson 2.10.1
- **AI模型**: 智谱 GLM-4-Flash

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
| AI助手 | /api/ai | AI智能对话 |

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
    url: jdbc:mysql://localhost:3307/book_trading
    username: your_username  # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码

# AI智能助手配置（可选）
ai:
  zhipu:
    api-key: your_api_key  # 智谱AI的API密钥，留空则使用本地规则引擎
    model: glm-4-flash     # 使用的模型
```

> **AI功能说明**: 
> - 如果配置了智谱AI的API密钥，将使用GLM-4模型提供智能对话
> - 如果未配置，系统会自动降级到本地规则引擎，仍可提供基础问答功能

### 前端配置 (.env.development)

```bash
VITE_API_BASE_URL=/api  # API基础路径
```

## 生产部署

### 方式一：传统部署

#### 后端打包

```bash
cd backgrond
mvn clean package -DskipTests
# 生成 target/book-trading-1.0.0.jar
```

#### 前端打包

```bash
cd frontend
npm run build
# 生成 dist/ 目录
```

#### 部署步骤

1. **后端部署**
```bash
# 上传 jar 包到服务器
scp target/book-trading-1.0.0.jar user@server:/opt/app/

# 启动服务
java -jar book-trading-1.0.0.jar --spring.profiles.active=prod
```

2. **前端部署**
```bash
# 上传 dist 目录到服务器
scp -r dist/* user@server:/var/www/html/
```

3. **Nginx 配置**
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # API反向代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### 方式二：Docker 部署

#### 后端 Dockerfile

项目已包含 `backgrond/Dockerfile`：

```bash
cd backgrond
docker build -t book-trading-backend .
docker run -d -p 8080:8080 \
  -e MYSQL_HOST=your_mysql_host \
  -e MYSQL_PORT=3306 \
  -e MYSQL_DATABASE=book_trading \
  -e MYSQL_USER=root \
  -e MYSQL_PASSWORD=your_password \
  book-trading-backend
```

#### 前端 Dockerfile

项目已包含 `frontend/Dockerfile`：

```bash
cd frontend
docker build -t book-trading-frontend .
docker run -d -p 80:80 book-trading-frontend
```

### 方式三：Railway 云平台部署

详细步骤请参考项目根目录的 `Railway部署指南.md` 文档。

**优势：**
- 免费额度充足（每月 $5）
- 自动化部署（Git 推送即部署）
- 提供 MySQL 数据库
- 自动生成域名
- 无需服务器运维

**快速开始：**
1. 推送代码到 GitHub
2. 在 Railway 创建项目
3. 添加 MySQL 服务
4. 部署后端和前端
5. 配置环境变量

### 环境变量配置

#### 后端环境变量（生产环境）

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| MYSQL_HOST | 数据库主机 | localhost |
| MYSQL_PORT | 数据库端口 | 3306 |
| MYSQL_DATABASE | 数据库名 | book_trading |
| MYSQL_USER | 数据库用户 | root |
| MYSQL_PASSWORD | 数据库密码 | your_password |
| JWT_SECRET | JWT密钥 | your_secret_key |
| AI_API_KEY | 智谱AI密钥 | your_api_key |

#### 前端环境变量（生产环境）

修改 `frontend/.env.production`：

```bash
# 后端API地址（根据实际部署修改）
VITE_API_BASE_URL=https://your-backend-domain.com/api
```

### 部署检查清单

部署前请确认：

- [ ] 数据库已创建并导入初始数据
- [ ] 生产环境配置文件已正确配置
- [ ] 敏感信息（密码、密钥）已使用环境变量
- [ ] 前端 API 地址已指向生产环境后端
- [ ] 防火墙已开放必要端口（80、443、8080）
- [ ] SSL 证书已配置（推荐使用 Let's Encrypt）
- [ ] 日志目录已创建且有写入权限
- [ ] 定期备份策略已设置

### 性能优化建议

1. **数据库优化**
   - 为常用查询字段添加索引
   - 定期清理过期数据
   - 使用连接池（HikariCP）

2. **后端优化**
   - 启用 Gzip 压缩
   - 配置合理的 JVM 参数
   - 使用 Redis 缓存热点数据

3. **前端优化**
   - 启用 CDN 加速
   - 图片懒加载
   - 路由懒加载
   - 开启 Gzip 压缩

4. **Nginx 优化**
   - 启用缓存
   - 配置 Gzip
   - 限流防刷

## 功能特性详解

### 1. AI 智能助手

系统集成了智谱 GLM-4 大语言模型，提供智能客服功能：

**核心能力：**
- 📊 **实时数据查询**: 自动查询数据库，提供准确的平台统计、书籍信息
- 🔍 **智能搜索**: 支持自然语言查询，如"50元以下的书有哪些"
- 💡 **使用指南**: 解答平台使用问题，提供操作指引
- 🤖 **上下文记忆**: 支持多轮对话，理解上下文语境
- 🎯 **降级策略**: API不可用时自动切换到本地规则引擎

**使用方式：**
- 点击页面右下角的悬浮按钮打开AI助手
- 输入问题即可获得智能回复
- 支持快捷问题一键提问

### 2. 书籍成色分级

系统采用标准化的书籍成色评级：

| 等级 | 说明 | 适用场景 |
|------|------|---------|
| 全新 | 未拆封，完全未使用 | 收藏级、礼品 |
| 九成新 | 几乎无使用痕迹 | 轻度阅读 |
| 八成新 | 轻微使用痕迹 | 正常阅读 |
| 七成新 | 有明显使用痕迹 | 学习参考 |
| 六成新及以下 | 较多使用痕迹 | 实用阅读 |

### 3. 订单流程

完整的订单生命周期管理：

```
创建订单 → 卖家确认 → 买家支付 → 卖家发货 → 买家确认收货 → 交易完成 → 互相评价
   ↓          ↓          ↓          ↓           ↓
 可取消     可取消     可取消     可取消      不可取消
```

### 4. 信誉体系

- 用户评分：基于交易评价的平均分（1-5星）
- 评价数量：累计收到的评价总数
- 信誉展示：在用户主页和书籍详情页展示

### 5. 权限控制

| 角色 | 权限 |
|------|------|
| 游客 | 浏览书籍、查看详情 |
| 普通用户 | 发布书籍、购买、收藏、消息、评价 |
| 管理员 | 用户管理、分类管理、数据统计 |

## 常见问题

### Q1: 数据库连接失败
**症状：** 后端启动报错 `Communications link failure`

**解决方案：**
1. 检查 MySQL 服务是否启动：`systemctl status mysql` (Linux) 或任务管理器 (Windows)
2. 检查端口是否正确：默认 3307（Docker）或 3306（本地）
3. 检查用户名密码：在 `application-dev.yml` 中配置
4. 检查数据库是否存在：`SHOW DATABASES;`

### Q2: 前端无法访问后端API
**症状：** 浏览器控制台显示 `Network Error` 或 `404`

**解决方案：**
1. 确认后端已启动：访问 http://localhost:8080/api/doc.html
2. 检查 Vite 代理配置：`vite.config.ts` 中的 proxy 设置
3. 检查端口占用：`netstat -ano | findstr 8080` (Windows)
4. 清除浏览器缓存并重启前端服务

### Q3: JWT Token 过期
**症状：** 登录后一段时间自动退出

**说明：**
- 默认 Token 有效期为 8 小时
- 可在 `application.yml` 中修改 `app.jwt.expiration`（单位：毫秒）
- 建议生产环境设置为 2-4 小时

### Q4: AI 助手无法使用
**症状：** AI 回复"抱歉，我暂时无法回答"

**解决方案：**
1. 检查是否配置了智谱 API Key
2. 如未配置，系统会使用本地规则引擎（功能有限）
3. 获取 API Key：访问 https://open.bigmodel.cn
4. 配置到 `application.yml` 的 `ai.zhipu.api-key`

### Q5: 图片上传失败
**症状：** 发布书籍时图片无法上传

**解决方案：**
1. 检查上传目录权限：`./uploads` 需要可写权限
2. 检查文件大小：单个文件不超过 10MB
3. 检查文件格式：仅支持 jpg、jpeg、png、gif、webp
4. 检查磁盘空间是否充足

### Q6: 前端构建失败
**症状：** `npm run build` 报错

**解决方案：**
1. 删除 `node_modules` 和 `package-lock.json`
2. 重新安装：`npm install`
3. 检查 Node.js 版本：需要 18.x+
4. 清除缓存：`npm cache clean --force`

## 开发说明

### 代码规范
- **后端**: 遵循 Alibaba Java 开发手册
- **前端**: 使用 ESLint + Prettier 进行代码格式化
- **命名规范**: 
  - 类名：大驼峰（PascalCase）
  - 方法/变量：小驼峰（camelCase）
  - 常量：全大写下划线分隔（UPPER_SNAKE_CASE）

### Git 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整（不影响功能）
refactor: 重构（不是新功能也不是修复bug）
perf: 性能优化
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 开发工具推荐

**后端开发：**
- IDE: IntelliJ IDEA
- 数据库工具: Navicat / DBeaver
- API测试: Postman / Apifox

**前端开发：**
- IDE: VS Code
- 浏览器: Chrome + Vue DevTools
- 调试工具: Vue DevTools、Network 面板

### 项目文档

- `AI智能体说明文档.md`: AI助手功能详细说明
- `Railway部署指南.md`: Railway云平台部署教程
- `GitHub推送指南.md`: Git操作指南

## 技术亮点

1. **前后端分离架构**: 清晰的职责划分，便于团队协作和独立部署
2. **JWT 无状态认证**: 支持分布式部署，无需 Session 共享
3. **MyBatis-Plus**: 简化 CRUD 操作，提供强大的条件构造器
4. **统一响应格式**: 规范的 API 响应，便于前端统一处理
5. **全局异常处理**: 优雅的异常处理机制，避免敏感信息泄露
6. **AI 智能助手**: 集成大语言模型，提供智能客服功能
7. **Docker 支持**: 提供完整的 Dockerfile，支持容器化部署
8. **多环境配置**: 开发、测试、生产环境配置分离

## 项目截图

> 待补充：可添加系统主要页面的截图

## 更新日志

### v1.0.0 (2024-01-15)
- ✨ 完成核心功能开发
- ✨ 集成 AI 智能助手
- ✨ 支持 Docker 和 Railway 部署
- 📝 完善项目文档

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 联系方式

- 项目地址: https://github.com/jizhaoyu/demo2
- 问题反馈: 提交 Issue

## 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [MyBatis-Plus](https://baomidou.com/)
- [Naive UI](https://www.naiveui.com/)
- [智谱AI](https://open.bigmodel.cn/)

## License

MIT License

Copyright (c) 2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
