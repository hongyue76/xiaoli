#!/bin/bash

# ========================================
#   律法先锋 - 法律AI智能平台
#   一键启动脚本 v1.0
# ========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================"
echo "  律法先锋 - 法律AI智能平台"
echo "  一键启动脚本 v1.0"
echo "========================================"
echo ""

# ===== 检查环境 =====
echo -e "${YELLOW}[步骤 1/6] 检查运行环境...${NC}"

# 检查 Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}[错误] 未检测到 Java，请先安装 JDK 17+${NC}"
    echo "下载地址: https://adoptium.net/"
    exit 1
fi
echo -e "${GREEN}[OK] Java 已安装${NC}"

# 检查 Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}[错误] 未检测到 Maven，请先安装${NC}"
    echo "下载地址: https://maven.apache.org/download.cgi"
    exit 1
fi
echo -e "${GREEN}[OK] Maven 已安装${NC}"

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}[错误] 未检测到 Node.js，请先安装${NC}"
    echo "下载地址: https://nodejs.org/"
    exit 1
fi
echo -e "${GREEN}[OK] Node.js 已安装${NC}"

# 检查 Docker
if ! docker ps &> /dev/null; then
    echo -e "${RED}[错误] Docker 未运行，请先启动 Docker${NC}"
    exit 1
fi
echo -e "${GREEN}[OK] Docker 已运行${NC}"

echo ""

# ===== 第2步：启动数据库 =====
echo -e "${YELLOW}[步骤 2/6] 启动数据库服务...${NC}"

# PostgreSQL
if docker ps -a | grep -q xiaoli-postgres; then
    if ! docker ps | grep -q xiaoli-postgres; then
        echo "[启动] PostgreSQL..."
        docker start xiaoli-postgres
    else
        echo "[跳过] PostgreSQL 已运行"
    fi
else
    echo "[创建] PostgreSQL 容器..."
    docker run -d --name xiaoli-postgres \
        -e POSTGRES_DB=xiaoli_legal \
        -e POSTGRES_USER=xiaoli \
        -e POSTGRES_PASSWORD=Xiaoli@2024 \
        -p 5432:5432 \
        postgres:15
fi
echo -e "${GREEN}[OK] PostgreSQL 就绪${NC}"

# Redis
if docker ps -a | grep -q xiaoli-redis; then
    if ! docker ps | grep -q xiaoli-redis; then
        echo "[启动] Redis..."
        docker start xiaoli-redis
    else
        echo "[跳过] Redis 已运行"
    fi
else
    echo "[创建] Redis 容器..."
    docker run -d --name xiaoli-redis \
        -p 6379:6379 \
        redis:7-alpine
fi
echo -e "${GREEN}[OK] Redis 就绪${NC}"

echo ""
echo "[等待] 数据库启动中..."
sleep 8

# ===== 第3步：安装前端依赖 =====
echo -e "${YELLOW}[步骤 3/6] 安装前端依赖...${NC}"
cd frontend/web
if [ ! -d "node_modules" ]; then
    echo "[安装] npm 依赖包..."
    npm install
else
    echo "[跳过] node_modules 已存在"
fi
echo -e "${GREEN}[OK] 前端依赖安装完成${NC}"
cd ../..

echo ""

# ===== 第4步：编译后端 =====
echo -e "${YELLOW}[步骤 4/6] 编译后端服务...${NC}"
cd backend
if [ ! -f "ms-consult/target/ms-consult-1.0.0.jar" ]; then
    echo "[编译] Maven 项目..."
    mvn clean package -DskipTests -q
else
    echo "[跳过] 后端 jar 包已存在"
fi
echo -e "${GREEN}[OK] 后端编译完成${NC}"

# ===== 第5步：加载环境变量 =====
echo -e "${YELLOW}[步骤 5/6] 加载环境变量...${NC}"
if [ -f ".env" ]; then
    echo "[加载] 从 .env 文件读取配置..."
    set -a
    source .env
    set +a
    echo -e "${GREEN}[OK] 环境变量已加载${NC}"
else
    echo -e "${YELLOW}[提示] 未找到 .env 文件，使用默认配置${NC}"
fi

echo ""

# ===== 第6步：启动后端服务 =====
echo -e "${YELLOW}[步骤 6/6] 启动后端微服务...${NC}"

echo "[启动] ms-consult (18081)..."
java -jar ms-consult/target/ms-consult-1.0.0.jar --server.port=18081 &
PID_MS_CONSULT=$!

echo "[启动] ms-document (18082)..."
java -jar ms-document/target/ms-document-1.0.0.jar --server.port=18082 &
PID_MS_DOCUMENT=$!

echo "[启动] ms-case (18083)..."
java -jar ms-case/target/ms-caseinfo-1.0.0.jar --server.port=18083 &
PID_MS_CASE=$!

echo "[启动] ms-contract (18084)..."
java -jar ms-contract/target/ms-contract-1.0.0.jar --server.port=18084 &
PID_MS_CONTRACT=$!

echo "[启动] ms-analysis (18085)..."
java -jar analysis/target/ms-analysis-1.0.0.jar --server.port=18085 &
PID_MS_ANALYSIS=$!

echo "[启动] ms-decision (18086)..."
java -jar ms-decision/target/ms-decision-1.0.0.jar --server.port=18086 &
PID_MS_DECISION=$!

echo "[启动] ms-compliance (18087)..."
java -jar ms-compliance/target/ms-compliance-1.0.0.jar --server.port=18087 &
PID_MS_COMPLIANCE=$!

echo "[启动] ms-evidence (18088)..."
java -jar ms-evidence/target/ms-evidence-1.0.0.jar --server.port=18088 &
PID_MS_EVIDENCE=$!

echo "[启动] ms-speech (18089)..."
java -jar ms-speech/target/ms-speech-1.0.0.jar --server.port=18089 &
PID_MS_SPEECH=$!

echo ""
echo "[等待] 后端服务启动中（约需 1-2 分钟）..."

# ===== 启动前端 =====
echo ""
echo -e "${YELLOW}[启动] 前端服务...${NC}"
cd ../frontend/web
npm run dev &
PID_FRONTEND=$!

echo ""
echo "[等待] 前端服务启动中..."
sleep 10

# ===== 打开浏览器 =====
echo ""
echo "[打开] 浏览器..."
if [[ "$OSTYPE" == "darwin"* ]]; then
    open http://localhost:3000
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    xdg-open http://localhost:3000
fi

# ===== 完成 =====
echo ""
echo "========================================"
echo -e "${GREEN}  启动完成！${NC}"
echo "========================================"
echo ""
echo "服务地址："
echo "  - 前端界面:  http://localhost:3000"
echo "  - 咨询API:   http://localhost:18081"
echo "  - 文书API:   http://localhost:18082"
echo "  - 案例API:   http://localhost:18083"
echo "  - 数据库:    localhost:5432"
echo "  - Redis:     localhost:6379"
echo ""
echo "提示："
echo "  - 查看后端窗口确认服务启动状态"
echo "  - 如需配置 API Key，编辑 backend/.env 文件"
echo ""
echo "停止服务请运行: ./stop.sh"
echo ""

# 保存 PIDs 以便后续停止
echo $PID_MS_CONSULT $PID_MS_DOCUMENT $PID_MS_CASE $PID_MS_CONTRACT > .pids
echo $PID_MS_ANALYSIS $PID_MS_DECISION $PID_MS_COMPLIANCE $PID_MS_EVIDENCE >> .pids
echo $PID_MS_SPEECH $PID_FRONTEND >> .pids
