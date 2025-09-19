# 古力商城前端 Docker 部署指南

## 📋 部署概述

本指南提供了renren-fast-vue前端项目的标准Docker部署方案，不依赖Nginx Proxy Manager，可直接通过端口访问。

## 🛠️ 环境要求

- Docker 已安装并运行
- Docker Compose 已安装（可选）
- 服务器端口 8001 可访问

## 🚀 快速部署

### 方法一：使用部署脚本（推荐）

```bash
# 1. 进入前端项目目录
cd renren-fast-vue

# 2. 给脚本执行权限
chmod +x deploy.sh

# 3. 构建并部署
./deploy.sh

# 4. 访问应用
# http://your-server-ip:8001
```

### 方法二：使用 Docker Compose

```bash
# 1. 进入前端项目目录
cd renren-fast-vue

# 2. 启动服务
docker-compose up -d --build

# 3. 查看状态
docker-compose ps
```

### 方法三：使用 Docker 命令

```bash
# 1. 构建镜像
docker build -t gulimall/renren-fast-vue:latest .

# 2. 创建日志目录
mkdir -p logs

# 3. 创建日志目录并运行容器
# 注意：需要在renren-fast-vue项目目录下执行
mkdir -p logs

docker run -d \
  --name renren-fast-vue \
  -p 8001:80 \
  --restart unless-stopped \
  -v $(pwd)/logs:/var/log/nginx \
  gulimall/renren-fast-vue:latest

# 或者指定完整路径（推荐生产环境）
# docker run -d \
#   --name renren-fast-vue \
#   -p 8001:80 \
#   --restart unless-stopped \
#   -v /var/log/renren-fast-vue:/var/log/nginx \
#   gulimall/renren-fast-vue:latest

# 4. 查看状态
docker ps | grep renren-fast-vue
```

## 📦 部署脚本使用说明

部署脚本 `deploy.sh` 提供了完整的容器管理功能：

### 基本命令

```bash
# 完整部署（构建+运行）
./deploy.sh

# 指定版本
./deploy.sh v1.0.0

# 只构建镜像
./deploy.sh latest build

# 使用docker-compose部署
./deploy.sh latest compose

# 查看帮助
./deploy.sh help
```

### 管理命令

```bash
# 查看状态
./deploy.sh status

# 查看日志
./deploy.sh logs

# 健康检查
./deploy.sh health

# 重启服务
./deploy.sh restart

# 更新应用
./deploy.sh update

# 进入容器
./deploy.sh enter

# 清理资源
./deploy.sh cleanup
```

## 🔧 配置说明

### 端口配置

- **容器端口**: 80
- **映射端口**: 8001
- **访问地址**: http://your-server-ip:8001

如需修改端口，编辑以下文件：
- `docker-compose.yml`: 修改 `ports` 部分
- `deploy.sh`: 修改 `HOST_PORT` 变量

### API代理配置

前端需要访问后端API时，修改 `nginx.conf` 中的代理配置：

```nginx
location /api/ {
    # 修改为您的后端服务地址
    proxy_pass http://your-backend-server:88/api/;
    # ... 其他配置
}
```

### 文件目录结构

```
renren-fast-vue/
├── Dockerfile              # Docker镜像构建文件
├── docker-compose.yml      # Docker Compose配置
├── nginx.conf              # Nginx服务器配置
├── deploy.sh               # 部署管理脚本
├── .dockerignore           # Docker构建忽略文件
├── logs/                   # Nginx日志目录（运行后生成）
└── dist/                   # 构建产物（构建后生成）
```

## 🔍 监控和日志

### 查看容器状态

```bash
# 使用脚本
./deploy.sh status

# 使用Docker命令
docker ps | grep renren-fast-vue
```

### 查看应用日志

```bash
# 使用脚本（实时日志）
./deploy.sh logs

# 使用Docker命令
docker logs -f renren-fast-vue
```

### 查看Nginx日志

```bash
# 访问日志
tail -f logs/access.log

# 错误日志
tail -f logs/error.log
```

### 健康检查

```bash
# 使用脚本
./deploy.sh health

# 手动检查
curl http://localhost:8001/health
```

## 🛠️ 常见问题解决

### 1. 端口被占用

**问题**: 启动时提示端口8001被占用

**解决**:
```bash
# 查看占用端口的进程
netstat -tulpn | grep :8001

# 停止占用端口的容器
docker stop $(docker ps -q --filter "publish=8001")

# 或修改映射端口
# 编辑 docker-compose.yml 或 deploy.sh
```

### 2. 容器启动失败

**问题**: 容器无法启动或立即退出

**解决**:
```bash
# 查看详细错误信息
docker logs renren-fast-vue

# 检查镜像是否构建成功
docker images | grep gulimall/renren-fast-vue

# 重新构建
./deploy.sh rebuild
```

### 3. 无法访问页面

**问题**: 浏览器无法打开页面

**解决**:
- 检查防火墙设置，确保8001端口开放
- 检查容器是否正常运行：`docker ps`
- 检查健康检查：`./deploy.sh health`
- 查看Nginx错误日志：`tail -f logs/error.log`

### 4. API请求失败

**问题**: 前端无法调用后端API

**解决**:
- 检查后端服务是否正常运行
- 确认 `nginx.conf` 中的API代理配置正确
- 检查网络连接和防火墙设置

### 5. 构建速度慢

**问题**: Docker镜像构建时间过长

**解决**:
- 使用国内npm镜像源（已配置）
- 配置Docker镜像加速器
- 使用 `.dockerignore` 减少构建上下文

## 🔒 安全配置

### 1. 防火墙设置

```bash
# CentOS/RHEL
firewall-cmd --permanent --add-port=8001/tcp
firewall-cmd --reload

# Ubuntu/Debian
ufw allow 8001
```

### 2. SSL配置（可选）

如需HTTPS访问，可以：
1. 在Nginx配置中添加SSL证书
2. 使用反向代理（如Nginx、Apache）
3. 使用云服务商的负载均衡器

## 📈 性能优化

### 1. 启用Gzip压缩

在 `nginx.conf` 中已启用Gzip压缩，可以：
- 减少传输大小
- 提高加载速度
- 降低带宽消耗

### 2. 静态资源缓存

已配置1年缓存策略：
```nginx
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

### 3. 容器资源限制

在生产环境中建议设置资源限制：

```yaml
# docker-compose.yml
deploy:
  resources:
    limits:
      memory: 512M
      cpus: '0.5'
```

## 🔄 更新和维护

### 应用更新

```bash
# 拉取最新代码
git pull

# 更新应用
./deploy.sh update
```

### 备份和恢复

```bash
# 备份镜像
./deploy.sh backup

# 恢复镜像
docker load < gulimall_renren-fast-vue_backup_20231201_143000.tar
```

### 定期维护

```bash
# 清理未使用的资源
docker system prune

# 查看磁盘使用
docker system df
```

## 📞 支持信息

如遇到问题，请：
1. 查看容器日志：`./deploy.sh logs`
2. 检查健康状态：`./deploy.sh health`
3. 查看部署脚本帮助：`./deploy.sh help`
