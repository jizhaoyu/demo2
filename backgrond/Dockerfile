# =============================================
# 二手书交易平台 - 后端 Dockerfile
# =============================================

# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 创建上传目录
RUN mkdir -p /app/uploads

# 复制构建产物
COPY --from=build /app/target/*.jar app.jar

# Railway 会自动注入 PORT 环境变量
ENV PORT=8080
EXPOSE ${PORT}

# 启动命令
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=railway", "-Dserver.port=${PORT}", "app.jar"]
