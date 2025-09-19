# 多阶段构建 Dockerfile for renren-fast-vue
FROM node:12.19.0 as builder

# 设置工作目录
WORKDIR /app

# 设置npm镜像源（加速npm包下载）
RUN npm config set registry https://registry.npmmirror.com

# 复制package.json和package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production

# 复制源代码
COPY . .

# 构建项目
RUN npm run build

# 第二阶段：使用Nginx作为Web服务器
FROM nginx:1.10

# 从构建阶段复制构建结果到nginx目录
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制nginx配置文件
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
