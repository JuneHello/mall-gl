#!/bin/bash

# Docker部署脚本 for renren-fast-vue

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目信息
PROJECT_NAME="gulimall/renren-fast-vue"
VERSION=${1:-latest}
CONTAINER_NAME="renren-fast-vue"
HOST_PORT="8001"

echo -e "${BLUE}===========================================${NC}"
echo -e "${BLUE}    古力商城前端 Docker 部署脚本${NC}"
echo -e "${BLUE}===========================================${NC}"

# 函数：打印带颜色的消息
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 函数：检查Docker是否运行
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker未运行，请启动Docker服务"
        exit 1
    fi
    print_message "Docker服务正常运行"
}

# 函数：检查端口是否被占用
check_port() {
    if netstat -tuln 2>/dev/null | grep -q ":${HOST_PORT} "; then
        print_warning "端口 ${HOST_PORT} 已被占用"
        read -p "是否强制停止占用端口的容器? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            docker stop $(docker ps -q --filter "publish=${HOST_PORT}") 2>/dev/null || true
            print_message "已停止占用端口的容器"
        else
            print_error "请先释放端口 ${HOST_PORT} 或修改映射端口"
            exit 1
        fi
    fi
}

# 函数：构建Docker镜像
build_image() {
    print_message "开始构建Docker镜像..."
    print_message "镜像名称: ${PROJECT_NAME}:${VERSION}"

    docker build -t ${PROJECT_NAME}:${VERSION} .

    if [ $? -eq 0 ]; then
        print_message "镜像构建成功！"
        docker images | grep ${PROJECT_NAME}
    else
        print_error "镜像构建失败！"
        exit 1
    fi
}

# 函数：停止并删除旧容器
remove_old_container() {
    if [ "$(docker ps -aq -f name=${CONTAINER_NAME})" ]; then
        print_warning "发现旧容器，正在停止并删除..."
        docker stop ${CONTAINER_NAME} > /dev/null 2>&1 || true
        docker rm ${CONTAINER_NAME} > /dev/null 2>&1 || true
        print_message "旧容器已删除"
    fi
}

# 函数：创建必要的目录
create_directories() {
    print_message "创建必要的目录..."
    mkdir -p logs
    print_message "目录创建完成"
}

# 函数：运行容器
run_container() {
    print_message "启动容器..."
    docker run -d \
        --name ${CONTAINER_NAME} \
        -p ${HOST_PORT}:80 \
        --restart unless-stopped \
        -v $(pwd)/logs:/var/log/nginx \
        --label "project=gulimall" \
        --label "service=frontend" \
        ${PROJECT_NAME}:${VERSION}

    if [ $? -eq 0 ]; then
        print_message "容器启动成功！"
        print_message "访问地址: http://localhost:${HOST_PORT}"
        print_message "容器名称: ${CONTAINER_NAME}"
    else
        print_error "容器启动失败！"
        docker logs ${CONTAINER_NAME}
        exit 1
    fi
}

# 函数：使用docker-compose部署
deploy_with_compose() {
    print_message "使用docker-compose部署..."
    if [ ! -f "docker-compose.yml" ]; then
        print_error "未找到docker-compose.yml文件"
        exit 1
    fi

    create_directories
    docker-compose up -d --build

    if [ $? -eq 0 ]; then
        print_message "docker-compose部署成功！"
        show_status
    else
        print_error "docker-compose部署失败！"
        exit 1
    fi
}

# 函数：显示容器状态
show_status() {
    print_message "容器状态:"
    docker ps -f name=${CONTAINER_NAME} --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""
    print_message "镜像信息:"
    docker images | grep ${PROJECT_NAME} | head -5
}

# 函数：查看容器日志
show_logs() {
    print_message "容器日志 (最近50行):"
    if [ "$(docker ps -q -f name=${CONTAINER_NAME})" ]; then
        docker logs --tail 50 -f ${CONTAINER_NAME}
    else
        print_error "容器未运行"
    fi
}

# 函数：进入容器
enter_container() {
    print_message "进入容器..."
    if [ "$(docker ps -q -f name=${CONTAINER_NAME})" ]; then
        docker exec -it ${CONTAINER_NAME} /bin/sh
    else
        print_error "容器未运行"
    fi
}

# 函数：健康检查
health_check() {
    print_message "执行健康检查..."

    # 检查容器是否运行
    if ! docker ps | grep -q ${CONTAINER_NAME}; then
        print_error "容器未运行"
        return 1
    fi

    # 检查端口是否可访问
    if curl -s --max-time 10 http://localhost:${HOST_PORT}/health > /dev/null; then
        print_message "健康检查通过 - 服务正常"
        print_message "前端地址: http://localhost:${HOST_PORT}"
    else
        print_warning "健康检查失败 - 服务可能未完全启动"
        print_message "请检查容器日志: ./deploy.sh logs"
        return 1
    fi
}

# 函数：清理资源
cleanup() {
    print_message "清理Docker资源..."

    # 停止容器
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    docker rm ${CONTAINER_NAME} 2>/dev/null || true

    # 删除镜像
    read -p "是否删除镜像 ${PROJECT_NAME}? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker rmi ${PROJECT_NAME}:${VERSION} 2>/dev/null || true
        print_message "镜像已删除"
    fi

    # 清理未使用的资源
    read -p "是否清理未使用的Docker资源? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker system prune -f
        print_message "Docker资源清理完成"
    fi
}

# 函数：更新应用
update_app() {
    print_message "更新应用..."
    build_image
    remove_old_container
    run_container
    show_status
    print_message "应用更新完成"
}

# 函数：备份镜像
backup_image() {
    local backup_name="${PROJECT_NAME}_backup_$(date +%Y%m%d_%H%M%S).tar"
    print_message "备份镜像到: ${backup_name}"
    docker save ${PROJECT_NAME}:${VERSION} | gzip > ${backup_name}
    print_message "镜像备份完成"
}

# 函数：显示帮助信息
show_help() {
    echo "用法: $0 [版本号] [选项]"
    echo ""
    echo "选项:"
    echo "  build              只构建镜像"
    echo "  run                只运行容器"
    echo "  compose            使用docker-compose部署"
    echo "  rebuild            重新构建并运行"
    echo "  update             更新应用"
    echo "  stop               停止容器"
    echo "  start              启动已存在的容器"
    echo "  restart            重启容器"
    echo "  logs               查看日志"
    echo "  status             查看状态"
    echo "  health             健康检查"
    echo "  enter              进入容器"
    echo "  backup             备份镜像"
    echo "  cleanup            清理资源"
    echo "  help               显示帮助"
    echo ""
    echo "示例:"
    echo "  $0                 构建并运行 (版本latest)"
    echo "  $0 v1.0.0          构建并运行 (版本v1.0.0)"
    echo "  $0 latest build    只构建镜像"
    echo "  $0 latest compose  使用docker-compose"
    echo "  $0 latest logs     查看日志"
    echo ""
    echo "访问地址: http://localhost:${HOST_PORT}"
}

# 主程序
main() {
    check_docker

    case "${2:-all}" in
        "build")
            build_image
            ;;
        "run")
            check_port
            create_directories
            remove_old_container
            run_container
            show_status
            health_check
            ;;
        "compose")
            check_port
            deploy_with_compose
            health_check
            ;;
        "rebuild")
            build_image
            check_port
            create_directories
            remove_old_container
            run_container
            show_status
            health_check
            ;;
        "update")
            update_app
            health_check
            ;;
        "stop")
            docker stop ${CONTAINER_NAME} 2>/dev/null || true
            print_message "容器已停止"
            ;;
        "start")
            docker start ${CONTAINER_NAME} 2>/dev/null || {
                print_error "容器不存在，请先构建并运行"
                exit 1
            }
            print_message "容器已启动"
            health_check
            ;;
        "restart")
            docker restart ${CONTAINER_NAME} 2>/dev/null || {
                print_error "容器不存在，请先构建并运行"
                exit 1
            }
            print_message "容器已重启"
            health_check
            ;;
        "logs")
            show_logs
            ;;
        "status")
            show_status
            ;;
        "health")
            health_check
            ;;
        "enter")
            enter_container
            ;;
        "backup")
            backup_image
            ;;
        "cleanup")
            cleanup
            ;;
        "help")
            show_help
            ;;
        "all"|*)
            build_image
            check_port
            create_directories
            remove_old_container
            run_container
            show_status
            health_check
            ;;
    esac
}

# 如果直接运行脚本
if [ "${BASH_SOURCE[0]}" == "${0}" ]; then
    main "$@"
fi
