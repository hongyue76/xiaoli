#!/bin/bash

# ===========================================
# 律法先锋 - 腾讯云学生机一键部署脚本
# ===========================================
# 
# 使用方法:
#   1. 上传到服务器: scp -r deployment/docker root@你的IP:/root/
#   2. SSH连接服务器: ssh root@你的IP
#   3. 进入目录: cd /root/docker
#   4. 运行脚本: bash deploy.sh
#
# ===========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}    律法先锋 - 一键部署脚本${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# 检查是否为 root 用户
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}请使用 root 用户运行此脚本${NC}"
    echo "提示: 运行 'sudo su' 切换到 root 用户"
    exit 1
fi

# 1. 检查并安装 Docker
echo -e "${YELLOW}[1/6] 检查 Docker 环境...${NC}"
if ! command -v docker &> /dev/null; then
    echo "Docker 未安装，正在安装..."
    curl -fsSL https://get.docker.com | sh
    systemctl start docker
    systemctl enable docker
    echo -e "${GREEN}Docker 安装完成${NC}"
else
    echo -e "${GREEN}Docker 已安装: $(docker --version)${NC}"
fi

# 2. 检查并安装 Docker Compose
echo -e "${YELLOW}[2/6] 检查 Docker Compose...${NC}"
if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose 未安装，正在安装..."
    curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    echo -e "${GREEN}Docker Compose 安装完成${NC}"
else
    echo -e "${GREEN}Docker Compose 已安装: $(docker-compose --version)${NC}"
fi

# 3. 创建配置目录
echo -e "${YELLOW}[3/6] 创建配置目录...${NC}"
mkdir -p /root/xiaoli
echo -e "${GREEN}配置目录创建完成${NC}"

# 4. 配置环境变量
echo -e "${YELLOW}[4/6] 配置环境变量...${NC}"

# 读取用户输入的 API 配置
read -p "请输入 DeepSeek API Key (sk-xxx): " DEEPSEEK_API_KEY
read -p "请输入得理法搜 APP_ID: " DELILEGAL_APP_ID
read -p "请输入得理法搜 SECRET: " DELILEGAL_SECRET

# 创建 .env 文件
cat > /root/xiaoli/.env << EOF
# ===========================================
# 律法先锋 - 环境变量配置
# ===========================================

# 数据库密码（建议修改）
DB_PASSWORD=Xiaoli@2024

# DeepSeek API（必须配置）
DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY}

# 得理法搜 API（必须配置）
DELILEGAL_APP_ID=${DELILEGAL_APP_ID}
DELILEGAL_SECRET=${DELILEGAL_SECRET}

# Redis（可选）
REDIS_PASSWORD=
EOF

echo -e "${GREEN}环境变量配置完成${NC}"

# 5. 复制部署文件
echo -e "${YELLOW}[5/6] 复制部署文件...${NC}"
cp /root/docker/docker-compose.simple.yml /root/xiaoli/
cp /root/docker/Dockerfile.backend /root/xiaoli/ 2>/dev/null || true
cp /root/docker/Dockerfile.frontend /root/xiaoli/ 2>/dev/null || true
cp -r /root/docker/init /root/xiaoli/ 2>/dev/null || true

echo -e "${GREEN}部署文件复制完成${NC}"

# 6. 启动服务
echo -e "${YELLOW}[6/6] 启动服务...${NC}"
cd /root/xiaoli

# 构建并启动（--no-cache 可选，用于强制重新构建）
docker-compose -f docker-compose.simple.yml up -d --build

# 等待服务启动
echo "等待服务启动..."
sleep 10

# 显示服务状态
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}    部署完成！${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "服务状态:"
docker-compose -f docker-compose.simple.yml ps
echo ""
echo -e "访问地址: ${YELLOW}http://你的服务器IP${NC}"
echo ""
echo -e "常用命令:"
echo "  - 查看日志: ${YELLOW}docker-compose -f docker-compose.simple.yml logs -f${NC}"
echo "  - 重启服务: ${YELLOW}docker-compose -f docker-compose.simple.yml restart${NC}"
echo "  - 停止服务: ${YELLOW}docker-compose -f docker-compose.simple.yml down${NC}"
echo ""
echo -e "${RED}请确保已在腾讯云控制台开放 80 端口！${NC}"
echo ""
