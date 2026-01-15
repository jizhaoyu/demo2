# 二手书交易管理系统 - 前端

基于 Vue 3 + Vite + Naive UI 的前端应用。

## 环境要求

- Node.js 18.x+
- npm 9.x+ 或 yarn 1.22+

## 快速启动

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

## 可用脚本

| 命令 | 说明 |
|------|------|
| `npm run dev` | 启动开发服务器 (端口3000) |
| `npm run build` | 构建生产版本 |
| `npm run build:check` | 类型检查后构建 |
| `npm run preview` | 预览构建结果 |
| `npm run lint` | ESLint代码检查 |
| `npm run type-check` | TypeScript类型检查 |

## 项目结构

```
src/
├── assets/              # 静态资源
│   └── styles/          # 全局样式
├── components/          # 公共组件
├── layouts/             # 布局组件
│   ├── MainLayout.vue   # 主布局 (导航栏+内容区)
│   └── AdminLayout.vue  # 管理后台布局 (侧边栏+内容区)
├── router/              # 路由配置
│   └── index.ts         # 路由规则和守卫
├── service/             # API接口封装
│   ├── auth.ts          # 认证接口
│   ├── user.ts          # 用户接口
│   ├── book.ts          # 书籍接口
│   ├── category.ts      # 分类接口
│   ├── favorite.ts      # 收藏接口
│   ├── order.ts         # 订单接口
│   ├── review.ts        # 评价接口
│   ├── message.ts       # 消息接口
│   └── admin.ts         # 管理接口
├── store/               # Pinia状态管理
│   └── user.ts          # 用户状态
├── types/               # TypeScript类型定义
│   └── index.ts         # 全局类型
├── utils/               # 工具函数
│   └── request.ts       # Axios封装
├── views/               # 页面组件
│   ├── auth/            # 认证页面
│   │   ├── Login.vue    # 登录
│   │   └── Register.vue # 注册
│   ├── book/            # 书籍页面
│   │   ├── BookList.vue     # 书籍列表
│   │   ├── BookDetail.vue   # 书籍详情
│   │   ├── BookPublish.vue  # 发布/编辑书籍
│   │   └── MyBooks.vue      # 我的书籍
│   ├── user/            # 用户页面
│   │   ├── Profile.vue      # 个人中心
│   │   ├── MyFavorites.vue  # 我的收藏
│   │   └── UserProfile.vue  # 用户主页
│   ├── order/           # 订单页面
│   │   ├── OrderList.vue    # 订单列表
│   │   ├── OrderDetail.vue  # 订单详情
│   │   └── OrderCreate.vue  # 创建订单
│   ├── message/         # 消息页面
│   │   ├── MessageCenter.vue # 消息中心
│   │   └── Chat.vue         # 聊天页面
│   ├── admin/           # 管理后台
│   │   ├── Dashboard.vue    # 数据统计
│   │   ├── UserManage.vue   # 用户管理
│   │   └── CategoryManage.vue # 分类管理
│   └── error/           # 错误页面
│       └── NotFound.vue     # 404页面
├── App.vue              # 根组件
└── main.ts              # 入口文件
```

## 环境配置

### 开发环境 (.env.development)

```bash
VITE_API_BASE_URL=/api
```

### 生产环境 (.env.production)

```bash
VITE_API_BASE_URL=/api
```

## 路由配置

### 公开路由 (无需登录)
- `/login` - 登录页
- `/register` - 注册页
- `/books` - 书籍列表
- `/book/:id` - 书籍详情
- `/user/:id` - 用户主页

### 需要登录的路由
- `/profile` - 个人中心
- `/my-books` - 我的书籍
- `/my-favorites` - 我的收藏
- `/book/publish` - 发布书籍
- `/orders` - 订单列表
- `/order/:id` - 订单详情
- `/messages` - 消息中心
- `/chat/:userId` - 聊天页面

### 管理员路由
- `/admin/dashboard` - 数据统计
- `/admin/users` - 用户管理
- `/admin/categories` - 分类管理

## 状态管理

使用 Pinia 管理全局状态:

```typescript
// store/user.ts
interface UserState {
  token: string | null
  userInfo: UserInfo | null
}

// 主要方法
- setToken(token: string)    // 设置Token
- setUserInfo(info: UserInfo) // 设置用户信息
- logout()                    // 登出
- isLoggedIn                  // 是否已登录
- isAdmin                     // 是否管理员
```

## API请求封装

使用 Axios 封装请求:

```typescript
// utils/request.ts
- 自动添加 Token 到请求头
- 统一处理响应数据
- 统一处理错误提示
- 401 自动跳转登录页
```

## UI组件库

使用 Naive UI 组件库:
- 按需引入组件
- 支持暗色主题
- 完整的 TypeScript 支持

## 开发规范

### 命名规范
- 组件文件: PascalCase (如 `BookList.vue`)
- 工具函数: camelCase (如 `formatDate`)
- 常量: UPPER_SNAKE_CASE (如 `API_BASE_URL`)

### 代码风格
- 使用 ESLint + Prettier 格式化
- 使用 TypeScript 类型注解
- 组件使用 Composition API

## 构建部署

```bash
# 构建
npm run build

# 输出目录: dist/
# 部署到 Nginx 或其他静态服务器
```

### Nginx配置

```nginx
location / {
    root /path/to/dist;
    index index.html;
    try_files $uri $uri/ /index.html;
}
```
