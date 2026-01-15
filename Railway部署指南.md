# Railway 部署指南 - 二手书交易平台

## 一、部署架构说明

```
┌─────────────────────────────────────────────────────────────────┐
│                        Railway 平台                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │   Frontend      │  │    Backend      │  │     MySQL       │  │
│  │   (Vue3+Vite)   │  │  (SpringBoot)   │  │    Database     │  │
│  │                 │  │                 │  │                 │  │
│  │  静态网站托管   │──│   Java 服务     │──│   云数据库      │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
│         │                     │                                  │
│         └──────────┬──────────┘                                  │
│                    │                                             │
└────────────────────┼─────────────────────────────────────────────┘
                     │
                     ▼
              https://你的域名.up.railway.app
```

**重要说明：** Railway 提供云端 MySQL 服务，你本地 Docker 中的 MySQL 数据需要迁移到 Railway 的 MySQL 中。

---

## 二、准备工作

### 2.1 注册 Railway 账号

1. 访问 [https://railway.app](https://railway.app)
2. 点击 "Login" → 选择 "GitHub" 登录
3. 授权 Railway 访问你的 GitHub

### 2.2 将项目推送到 GitHub

如果项目还没有推送到 GitHub：

```bash
# 在项目根目录执行
git init
git add .
git commit -m "Initial commit"

# 在 GitHub 创建仓库后
git remote add origin https://github.com/你的用户名/book-trading.git
git branch -M main
git push -u origin main
```

### 2.3 导出本地 MySQL 数据

在本地 Docker MySQL 中导出数据：

```bash
# 进入 Docker MySQL 容器
docker exec -it 你的mysql容器名 bash

# 导出数据库
mysqldump -u root -p book_trading > /tmp/book_trading.sql

# 退出容器后，复制到本地
docker cp 你的mysql容器名:/tmp/book_trading.sql ./book_trading.sql
```

或者使用 Navicat/DBeaver 等工具导出 SQL 文件。

---

## 三、Railway 部署步骤

### 第一步：创建 Railway 项目

1. 登录 Railway 后，点击 **"New Project"**
2. 选择 **"Empty Project"**（空项目）

---

### 第二步：添加 MySQL 数据库

1. 在项目中点击 **"+ New"** → **"Database"** → **"Add MySQL"**
2. Railway 会自动创建一个 MySQL 实例
3. 点击 MySQL 服务，查看 **"Variables"** 标签页，记录以下信息：
   - `MYSQL_HOST`
   - `MYSQL_PORT`
   - `MYSQL_DATABASE`
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
   - `DATABASE_URL`（完整连接字符串）

4. **导入数据：** 点击 **"Data"** 标签页 → **"Import"**，上传你导出的 `book_trading.sql` 文件

   或者使用命令行连接：
   ```bash
   mysql -h 你的MYSQL_HOST -P 端口 -u 用户名 -p 数据库名 < book_trading.sql
   ```

---

### 第三步：部署后端服务

#### 3.1 修改后端配置

在 `backgrond/src/main/resources/` 目录下创建 `application-railway.yml`：

```yaml
# Railway 环境配置
server:
  port: ${PORT:8080}

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}

# 生产环境日志级别
logging:
  level:
    com.example.booktrading: info
    org.springframework.web: warn

# 关闭SQL日志
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
```

#### 3.2 创建 Dockerfile（后端）

在 `backgrond/` 目录下创建 `Dockerfile`：

```dockerfile
# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Railway 会自动注入 PORT 环境变量
EXPOSE ${PORT:-8080}

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=railway", "app.jar"]
```

#### 3.3 创建 railway.json（后端）

在 `backgrond/` 目录下创建 `railway.json`：

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "Dockerfile"
  },
  "deploy": {
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

#### 3.4 在 Railway 部署后端

1. 在 Railway 项目中点击 **"+ New"** → **"GitHub Repo"**
2. 选择你的仓库
3. Railway 会检测到这是一个 monorepo，选择 **"backgrond"** 目录
4. 点击 **"Deploy"**

#### 3.5 配置后端环境变量

部署后，点击后端服务 → **"Variables"** 标签页，添加以下变量：

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `MYSQL_HOST` | （从MySQL服务复制） | 数据库主机 |
| `MYSQL_PORT` | （从MySQL服务复制） | 数据库端口 |
| `MYSQL_DATABASE` | （从MySQL服务复制） | 数据库名 |
| `MYSQL_USER` | （从MySQL服务复制） | 数据库用户 |
| `MYSQL_PASSWORD` | （从MySQL服务复制） | 数据库密码 |

**更简单的方式：** 点击 **"Add Reference"**，直接引用 MySQL 服务的变量。

#### 3.6 生成后端域名

1. 点击后端服务 → **"Settings"** 标签页
2. 在 **"Networking"** 部分，点击 **"Generate Domain"**
3. 记录生成的域名，如：`book-trading-backend.up.railway.app`

---

### 第四步：部署前端服务

#### 4.1 修改前端配置

修改 `frontend/.env.production`：

```bash
# 生产环境配置 - 指向 Railway 后端地址
VITE_API_BASE_URL=https://你的后端域名.up.railway.app/api
```

#### 4.2 创建 Dockerfile（前端）

在 `frontend/` 目录下创建 `Dockerfile`：

```dockerfile
# 构建阶段
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# 运行阶段 - 使用 nginx 托管静态文件
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### 4.3 创建 nginx.conf（前端）

在 `frontend/` 目录下创建 `nginx.conf`：

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # 支持 Vue Router 的 history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
}
```

#### 4.4 创建 railway.json（前端）

在 `frontend/` 目录下创建 `railway.json`：

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "Dockerfile"
  },
  "deploy": {
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

#### 4.5 在 Railway 部署前端

1. 在 Railway 项目中点击 **"+ New"** → **"GitHub Repo"**
2. 选择同一个仓库
3. 这次选择 **"frontend"** 目录
4. 点击 **"Deploy"**

#### 4.6 生成前端域名

1. 点击前端服务 → **"Settings"** 标签页
2. 在 **"Networking"** 部分，点击 **"Generate Domain"**
3. 这就是你的网站访问地址！

---

## 四、完整文件清单

部署前需要创建/修改的文件：

```
项目根目录/
├── backgrond/
│   ├── Dockerfile                              # 新建
│   ├── railway.json                            # 新建
│   └── src/main/resources/
│       └── application-railway.yml             # 新建
│
└── frontend/
    ├── Dockerfile                              # 新建
    ├── nginx.conf                              # 新建
    ├── railway.json                            # 新建
    └── .env.production                         # 修改
```

---

## 五、部署后验证

### 5.1 检查服务状态

在 Railway 控制台查看：
- 所有服务显示 **"Active"** 状态
- 查看 **"Deployments"** 标签页的构建日志，确认无错误

### 5.2 测试访问

1. **前端页面：** 访问 `https://你的前端域名.up.railway.app`
2. **后端API：** 访问 `https://你的后端域名.up.railway.app/api/doc.html`（Swagger文档）
3. **测试登录：** 使用测试账号登录验证

### 5.3 常见问题排查

| 问题 | 可能原因 | 解决方案 |
|------|---------|---------|
| 后端启动失败 | 数据库连接失败 | 检查环境变量是否正确配置 |
| 前端白屏 | API地址错误 | 检查 `.env.production` 中的后端地址 |
| 接口404 | 路径不对 | 确认后端 context-path 是 `/api` |
| CORS错误 | 跨域未配置 | 后端需要配置允许前端域名跨域 |

---

## 六、配置跨域（重要）

由于前后端分开部署，需要在后端配置 CORS。

修改 `backgrond/src/main/java/com/example/booktrading/config/WebMvcConfig.java`：

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",
                "https://你的前端域名.up.railway.app"  // 添加 Railway 前端域名
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

---

## 七、费用说明

Railway 提供：
- **免费额度：** 每月 $5 免费额度（约500小时运行时间）
- **Hobby Plan：** $5/月，无限运行时间
- **数据库：** MySQL 按使用量计费，小项目约 $1-3/月

**建议：** 开发测试阶段使用免费额度足够，正式上线后升级到 Hobby Plan。

---

## 八、后续维护

### 自动部署

Railway 默认开启自动部署，当你推送代码到 GitHub 时：
```bash
git add .
git commit -m "更新功能"
git push
```
Railway 会自动检测并重新部署。

### 查看日志

点击服务 → **"Deployments"** → 选择部署记录 → **"View Logs"**

### 回滚版本

在 **"Deployments"** 中找到之前的部署记录，点击 **"Redeploy"**

---

## 九、快速命令汇总

```bash
# 1. 推送代码到 GitHub
git add .
git commit -m "准备部署到Railway"
git push

# 2. 本地测试构建（可选）
cd backgrond && mvn clean package -DskipTests
cd frontend && npm run build

# 3. 导出本地数据库
docker exec mysql容器 mysqldump -u root -p book_trading > backup.sql
```

---

准备好这些文件后，按照步骤操作即可完成部署。有问题随时问我！
