# ==================== 前端 Dockerfile ====================
# 多阶段构建：构建阶段 + Nginx 部署

# ========== 构建阶段 ==========
FROM node:18-alpine AS builder

WORKDIR /app

# 先复制 package 文件利用 Docker 缓存
COPY package*.json ./
RUN npm ci --registry=https://registry.npmmirror.com

# 复制源代码并构建
COPY . .
RUN npm run build

# ========== 运行阶段 ==========
FROM nginx:alpine

# 复制自定义 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

# 从构建阶段复制静态文件
COPY --from=builder /app/dist /usr/share/nginx/html

# 创建 nginx 运行目录
RUN mkdir -p /var/cache/nginx /var/run

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/health || exit 1

# 暴露端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
