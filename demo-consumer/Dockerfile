# ==================== Consumer Dockerfile ====================
# 多阶段构建：构建阶段 + 运行阶段

# ========== 构建阶段 ==========
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app

# 先复制父 pom.xml 和子模块 pom.xml 利用 Docker 缓存
COPY pom.xml .
COPY demo-api/pom.xml demo-api/
COPY demo-consumer/pom.xml demo-consumer/

# 下载依赖
RUN mvn dependency:go-offline -B -pl demo-consumer -am

# 复制源代码并构建
COPY demo-api/src demo-api/src
COPY demo-consumer/src demo-consumer/src
RUN mvn clean package -DskipTests -B -pl demo-consumer -am

# ========== 运行阶段 ==========
FROM openjdk:8-jre-slim

WORKDIR /app

# 安装必要工具
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 复制项目中的 OpenTelemetry Java Agent
COPY opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# 从构建阶段复制 JAR
COPY --from=builder /app/demo-consumer/target/*.jar app.jar

# 创建非 root 用户运行应用
RUN groupadd -r spring && useradd -r -g spring spring
RUN chown -R spring:spring /app
USER spring:spring

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8889/api/remote/users?pageNum=1&pageSize=1 || exit 1

# 暴露端口
EXPOSE 8889

# JVM 优化参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# OpenTelemetry 配置 (可通过环境变量覆盖)
ENV OTEL_SERVICE_NAME="demo-consumer"
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://localhost:4318"
ENV OTEL_TRACES_EXPORTER="otlp"
ENV OTEL_METRICS_EXPORTER="otlp"
ENV OTEL_LOGS_EXPORTER="otlp"

# 启动命令 (根据 OTEL_ENABLED 决定是否使用 Java Agent)
ENV OTEL_ENABLED="true"
ENTRYPOINT ["sh", "-c", "if [ \"$OTEL_ENABLED\" = \"true\" ]; then java $JAVA_OPTS -javaagent:/app/opentelemetry-javaagent.jar -Dotel.service.name=$OTEL_SERVICE_NAME -Dotel.exporter.otlp.endpoint=$OTEL_EXPORTER_OTLP_ENDPOINT -Dotel.traces.exporter=$OTEL_TRACES_EXPORTER -Dotel.metrics.exporter=$OTEL_METRICS_EXPORTER -Dotel.logs.exporter=$OTEL_LOGS_EXPORTER -jar app.jar; else java $JAVA_OPTS -jar app.jar; fi"]
