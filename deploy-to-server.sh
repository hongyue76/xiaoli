#!/bin/bash

# ===========================================
# 律法先锋 - 一键拉取并部署脚本
# ===========================================
#
# 使用方法（服务器上执行）:
#   bash <(curl -sL https://raw.githubusercontent.com/hongyue76/xiaoli/main/deploy-to-server.sh)
#
# 或手动执行:
#   1. git clone https://github.com/hongyue76/xiaoli.git
#   2. cd xiaoli/deployment/docker
#   3. bash deploy.sh
#
# ===========================================

set -e

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  律法先锋 - 腾讯云一键部署${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# 检查 root
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}请使用 root 用户运行${NC}"
    exit 1
fi

# 安装 Docker
echo -e "${YELLOW}[1/4] 安装 Docker...${NC}"
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com | sh
    systemctl start docker
    systemctl enable docker
fi
echo -e "${GREEN}Docker 已安装${NC}"

# 安装 Docker Compose
echo -e "${YELLOW}[2/4] 安装 Docker Compose...${NC}"
if ! command -v docker-compose &> /dev/null; then
    curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
fi
echo -e "${GREEN}Docker Compose 已安装${NC}"

# 克隆项目
echo -e "${YELLOW}[3/4] 克隆项目...${NC}"
if [ ! -d "/root/xiaoli" ]; then
    cd /root
    git clone https://github.com/hongyue76/xiaoli.git
fi
cd /root/xiaoli
git pull
echo -e "${GREEN}项目已克隆${NC}"

# 配置并启动
echo -e "${YELLOW}[4/4] 配置并启动服务...${NC}"

read -p "DeepSeek API Key (sk-xxx): " DEEPSEEK_KEY
read -p "得理 APP_ID: " APP_ID
read -p "得理 SECRET: " SECRET

cat > /root/xiaoli/deployment/docker/.env << EOF
DB_PASSWORD=Xiaoli@2024
DEEPSEEK_API_KEY=${DEEPSEEK_KEY}
DELILEGAL_APP_ID=${APP_ID}
DELILEGAL_SECRET=${SECRET}
EOF

cd /root/xiaoli/deployment/docker
docker-compose -f docker-compose.simple.yml up -d --build

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  部署完成！${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "访问地址: http://你的服务器IP"
echo ""
docker-compose -f docker-compose.simple.yml ps
