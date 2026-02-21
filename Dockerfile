# ==================== 后端 Dockerfile ====================
# 多阶段构建：构建阶段 + 运行阶段

# ========== 构建阶段 ==========
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app

# 先复制 pom.xml 利用 Docker 缓存
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# ========== 运行阶段 ==========
FROM openjdk:8-jre-slim

WORKDIR /app

# 安装必要工具
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 从构建阶段复制 JAR
COPY --from=builder /app/target/*.jar app.jar

# 创建非 root 用户运行应用
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8888/api/auth/health || exit 1

# 暴露端口
EXPOSE 8888

# JVM 优化参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"]
