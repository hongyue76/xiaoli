#!/bin/bash
# 律法先锋 - Ubuntu 轻量应用服务器部署脚本

set -e

echo "=========================================="
echo "  律法先锋部署脚本"
echo "=========================================="

# 1. 更新系统
echo "[1/5] 更新系统..."
apt update && apt upgrade -y

# 2. 安装必要软件
echo "[2/5] 安装必要软件..."
apt install -y openjdk-17-jdk maven nginx postgresql postgresql-contrib redis-server git curl

# 3. 配置 PostgreSQL
echo "[3/5] 配置 PostgreSQL..."
sudo -u postgres psql << EOF
CREATE DATABASE xiaoli_legal;
CREATE USER xiaoli WITH PASSWORD 'Xiaoli@2024';
GRANT ALL PRIVILEGES ON DATABASE xiaoli_legal TO xiaoli;
\connect xiaoli_legal
GRANT ALL ON SCHEMA public TO xiaoli;
EOF

# 4. 克隆并构建项目
echo "[4/5] 克隆并构建项目..."
cd /opt
if [ -d "xiaoli" ]; then
    cd xiaoli && git pull
else
    git clone https://github.com/hongyue76/xiaoli.git
    cd xiaoli
fi

cd backend
mvn clean package -DskipTests

# 5. 创建启动脚本
echo "[5/5] 创建启动脚本..."
cat > /opt/xiaoli/start.sh << 'SCRIPT'
#!/bin/bash
# 启动所有微服务
cd /opt/xiaoli/backend

# 启动咨询微服务 (18081)
java -jar ms-consult/target/ms-consult-1.0.0.jar \
  --server.port=18081 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/xiaoli_legal \
  --spring.datasource.username=xiaoli \
  --spring.datasource.password=Xiaoli@2024 \
  --spring.data.redis.host=localhost \
  > /var/log/xiaoli/consult.log 2>&1 &

echo "微服务启动中..."
SCRIPT

chmod +x /opt/xiaoli/start.sh

echo ""
echo "=========================================="
echo "  部署完成!"
echo "=========================================="
echo "运行: /opt/xiaoli/start.sh 启动服务"
