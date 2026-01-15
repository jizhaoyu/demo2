# Railway 部署操作步骤 - 二手书交易平台

## 📋 部署前检查清单

- [x] 项目已推送到 GitHub
- [x] 已注册 Railway 账号
- [ ] 在 Railway 创建 MySQL 数据库
- [ ] 导入数据库表结构和数据
- [ ] 部署后端服务
- [ ] 部署前端服务
- [ ] 配置环境变量
- [ ] 测试访问

---

## 第一步：在 Railway 创建项目和 MySQL 数据库

### 1.1 创建新项目

1. 登录 [Railway](https://railway.app)
2. 点击 **"New Project"**
3. 选择 **"Empty Project"**
4. 给项目命名（如：book-trading-platform）

### 1.2 添加 MySQL 数据库

1. 在项目中点击 **"+ New"**
2. 选择 **"Database"** → **"Add MySQL"**
3. Railway 会自动创建 MySQL 实例
4. 等待数据库启动完成（状态变为 Active）

### 1.3 查看数据库连接信息

点击 MySQL 服务 → **"Variables"** 标签页，记录以下信息：

```
MYSQL_HOST=xxxxx.railway.app
MYSQL_PORT=3306
MYSQL_DATABASE=railway
MYSQL_USER=root
MYSQL_PASSWORD=xxxxxxxxxx
DATABASE_URL=mysql://root:password@host:port/railway
```

---

## 第二步：导入数据库表结构和数据

### 方式一：使用 Railway 的 Data 面板（推荐）

1. 点击 MySQL 服务 → **"Data"** 标签页
2. 点击 **"Query"** 按钮
3. 复制 `backgrond/src/main/resources/sql/schema.sql` 的内容
4. 粘贴到查询框，点击 **"Run"** 执行
5. 再复制 `backgrond/src/main/resources/sql/data.sql` 的内容
6. 粘贴到查询框，点击 **"Run"** 执行

### 方式二：使用本地 MySQL 客户端

```bash
# 使用 Railway 提供的连接信息
mysql -h xxxxx.railway.app -P 3306 -u root -p

# 输入密码后，执行以下命令
source backgrond/src/main/resources/sql/schema.sql
source backgrond/src/main/resources/sql/data.sql
```

### 方式三：使用 Navicat/DBeaver 等工具

1. 创建新连接，填入 Railway 的数据库信息
2. 连接成功后，运行 SQL 文件：
   - 先运行 `schema.sql`
   - 再运行 `data.sql`

### 验证数据导入

在 Railway 的 Data 面板执行：

```sql
-- 查看所有表
SHOW TABLES;

-- 查看用户数据
SELECT id, username, email, role FROM user;

-- 查看分类数据
SELECT * FROM category;

-- 查看书籍数量
SELECT COUNT(*) FROM book;
```

应该看到：
- 7 张表（user, category, book, favorite, orders, review, message）
- 6 个用户（1个管理员 + 5个测试用户）
- 6 个分类
- 25+ 本书籍

---

## 第三步：部署后端服务

### 3.1 在 Railway 添加后端服务

1. 在项目中点击 **"+ New"**
2. 选择 **"GitHub Repo"**
3. 选择你的仓库（如：book-trading）
4. Railway 会检测到这是 monorepo，选择 **"backgrond"** 目录
5. 点击 **"Deploy"**

### 3.2 配置后端环境变量

点击后端服务 → **"Variables"** 标签页，添加以下变量：

#### 方式一：手动添加（推荐新手）

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `MYSQL_HOST` | 从 MySQL 服务复制 | 数据库主机 |
| `MYSQL_PORT` | 从 MySQL 服务复制 | 数据库端口 |
| `MYSQL_DATABASE` | 从 MySQL 服务复制 | 数据库名 |
| `MYSQL_USER` | 从 MySQL 服务复制 | 数据库用户 |
| `MYSQL_PASSWORD` | 从 MySQL 服务复制 | 数据库密码 |
| `JWT_SECRET` | `bookTradingSecretKey2024VeryLongSecretKeyForJWTTokenGeneration` | JWT密钥 |
| `AI_API_KEY` | （可选）你的智谱AI密钥 | AI助手功能 |

#### 方式二：引用变量（推荐）

点击 **"Add Reference"**，选择 MySQL 服务，Railway 会自动关联所有数据库变量。

### 3.3 生成后端域名

1. 点击后端服务 → **"Settings"** 标签页
2. 找到 **"Networking"** 部分
3. 点击 **"Generate Domain"**
4. 记录生成的域名，例如：`book-trading-backend-production.up.railway.app`

### 3.4 验证后端部署

1. 查看 **"Deployments"** 标签页，确认构建成功
2. 访问 Swagger 文档：`https://你的后端域名/api/doc.html`
3. 应该能看到完整的 API 文档

---

## 第四步：部署前端服务

### 4.1 更新前端配置

**重要：** 在部署前端之前，必须先更新后端地址！

修改 `frontend/.env.production` 文件：

```bash
# 将下面的地址替换为你的 Railway 后端域名
VITE_API_BASE_URL=https://book-trading-backend-production.up.railway.app/api
```

提交并推送到 GitHub：

```bash
git add frontend/.env.production
git commit -m "更新生产环境后端地址"
git push
```

### 4.2 在 Railway 添加前端服务

1. 在项目中点击 **"+ New"**
2. 选择 **"GitHub Repo"**
3. 选择同一个仓库
4. 这次选择 **"frontend"** 目录
5. 点击 **"Deploy"**

### 4.3 生成前端域名

1. 点击前端服务 → **"Settings"** 标签页
2. 找到 **"Networking"** 部分
3. 点击 **"Generate Domain"**
4. 这就是你的网站访问地址！例如：`book-trading-frontend-production.up.railway.app`

### 4.4 配置 CORS（重要）

后端需要允许前端域名跨域访问。

修改 `backgrond/src/main/java/com/example/booktrading/config/WebMvcConfig.java`：

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",
                "https://book-trading-frontend-production.up.railway.app"  // 添加你的前端域名
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

提交并推送：

```bash
git add backgrond/src/main/java/com/example/booktrading/config/WebMvcConfig.java
git commit -m "配置CORS允许Railway前端域名"
git push
```

Railway 会自动检测到代码变更并重新部署后端。

---

## 第五步：测试访问

### 5.1 访问前端网站

打开浏览器，访问：`https://你的前端域名.up.railway.app`

### 5.2 测试登录

使用以下测试账号登录：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| 张三 | 123456 | 普通用户 |
| 李四 | 123456 | 普通用户 |

### 5.3 测试核心功能

- [ ] 用户登录/注册
- [ ] 浏览书籍列表
- [ ] 查看书籍详情
- [ ] 发布书籍（需登录）
- [ ] 收藏书籍
- [ ] 创建订单
- [ ] 发送消息
- [ ] 管理员后台（使用 admin 账号）

---

## 第六步：常见问题排查

### 问题1：后端启动失败

**症状：** Deployments 显示失败，日志中有数据库连接错误

**解决方案：**
1. 检查环境变量是否正确配置
2. 确认 MySQL 服务状态为 Active
3. 在 Railway 的 Data 面板测试数据库连接

### 问题2：前端白屏或接口404

**症状：** 前端页面空白，浏览器控制台显示接口请求失败

**解决方案：**
1. 检查 `.env.production` 中的后端地址是否正确
2. 确认后端服务已成功部署
3. 访问 `https://后端域名/api/doc.html` 确认后端可访问

### 问题3：CORS 跨域错误

**症状：** 浏览器控制台显示 CORS policy 错误

**解决方案：**
1. 确认 `WebMvcConfig.java` 中已添加前端域名
2. 重新部署后端服务
3. 清除浏览器缓存后重试

### 问题4：图片无法显示

**症状：** 书籍图片显示失败

**解决方案：**
- 测试数据使用的是豆瓣图片链接，可能被防盗链
- 可以上传自己的图片，或使用其他图床服务

### 问题5：AI 助手无法使用

**症状：** AI 助手功能报错

**解决方案：**
1. 确认已配置 `AI_API_KEY` 环境变量
2. 检查智谱 AI API 密钥是否有效
3. 如果不需要 AI 功能，可以忽略此错误

---

## 第七步：查看日志和监控

### 查看部署日志

1. 点击服务 → **"Deployments"** 标签页
2. 选择最新的部署记录
3. 点击 **"View Logs"** 查看详细日志

### 查看运行日志

1. 点击服务 → **"Logs"** 标签页
2. 实时查看应用运行日志
3. 可以搜索关键词定位问题

### 监控资源使用

1. 点击服务 → **"Metrics"** 标签页
2. 查看 CPU、内存、网络使用情况

---

## 第八步：后续维护

### 自动部署

Railway 默认开启自动部署，当你推送代码到 GitHub 时会自动重新部署：

```bash
# 修改代码后
git add .
git commit -m "更新功能"
git push
```

### 手动重新部署

1. 点击服务 → **"Deployments"** 标签页
2. 点击最新部署记录的 **"Redeploy"** 按钮

### 回滚版本

1. 在 **"Deployments"** 中找到之前的部署记录
2. 点击 **"Redeploy"** 回滚到该版本

### 暂停服务（节省费用）

1. 点击服务 → **"Settings"** 标签页
2. 找到 **"Danger"** 部分
3. 点击 **"Pause Service"** 暂停服务

---

## 费用说明

Railway 计费方式：

- **免费额度：** 每月 $5 免费额度（约 500 小时运行时间）
- **Hobby Plan：** $5/月，无限运行时间
- **数据库：** MySQL 按使用量计费，小项目约 $1-3/月

**建议：**
- 开发测试阶段使用免费额度
- 正式上线后升级到 Hobby Plan
- 定期清理不用的服务节省费用

---

## 快速命令汇总

```bash
# 1. 更新前端配置
# 编辑 frontend/.env.production，填入后端域名

# 2. 提交并推送
git add .
git commit -m "配置Railway部署"
git push

# 3. 本地测试构建（可选）
cd backgrond
mvn clean package -DskipTests

cd ../frontend
npm run build

# 4. 查看构建产物
ls backgrond/target/*.jar
ls frontend/dist/
```

---

## 部署架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    Railway 平台                              │
│                                                              │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │   Frontend   │    │   Backend    │    │    MySQL     │  │
│  │  (Nginx)     │───▶│ (SpringBoot) │───▶│  Database    │  │
│  │  Port: 80    │    │  Port: 8080  │    │  Port: 3306  │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
│         │                    │                              │
│         │                    │                              │
│  ┌──────▼────────────────────▼──────────────────────────┐  │
│  │              Railway 自动生成的域名                   │  │
│  │  frontend-xxx.up.railway.app                         │  │
│  │  backend-xxx.up.railway.app                          │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
                    用户通过浏览器访问
```

---

## 完成！

部署完成后，你将拥有：

✅ 一个可公网访问的前端网站  
✅ 一个稳定运行的后端 API 服务  
✅ 一个云端 MySQL 数据库  
✅ 自动部署功能（推送代码即部署）  
✅ 完整的日志和监控  

有问题随时查看 Railway 的日志，或者参考本文档的常见问题部分。

祝部署顺利！🎉
