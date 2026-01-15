# =============================================
# 二手书交易平台 - 前端 Dockerfile
# =============================================

# 构建阶段
FROM node:18-alpine AS build
WORKDIR /app

# 复制 package.json 并安装依赖（利用 Docker 缓存）
COPY package*.json ./
RUN npm ci

# 复制源码并构建
COPY . .
RUN npm run build

# 运行阶段 - 使用 nginx 托管静态文件
FROM nginx:alpine

# 复制构建产物
COPY --from=build /app/dist /usr/share/nginx/html

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Railway 使用 PORT 环境变量
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
