#!/bin/bash
# 律法先锋 - Ubuntu 部署脚本 (ubuntu用户版，使用sudo)
set -e

echo "=========================================="
echo "  律法先锋 一键部署 (ubuntu用户)"
echo "=========================================="

echo "[1/7] 检查 sudo 权限..."
sudo -v || exit 1
echo "sudo 权限正常"

echo "[2/7] 更新系统..."
sudo apt update -qq

echo "[3/7] 安装依赖..."
sudo apt install -y -qq openjdk-17-jdk maven postgresql postgresql-contrib redis-server nginx git curl

echo "[4/7] 验证安装..."
java -version 2>&1 | head -1
mvn -version 2>&1 | head -1

echo "[5/7] 配置 PostgreSQL..."
sudo systemctl start postgresql
sudo systemctl enable postgresql
sudo -u postgres psql -c "CREATE DATABASE xiaoli_legal;" 2>/dev/null || echo "数据库已存在"
sudo -u postgres psql -c "CREATE USER xiaoli WITH PASSWORD 'Xiaoli@2024';" 2>/dev/null || echo "用户已存在"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE xiaoli_legal TO xiaoli;" 2>/dev/null || true

echo "[6/7] 配置 Redis..."
sudo systemctl start redis-server
sudo systemctl enable redis-server

echo "[7/7] 克隆并构建项目..."
cd /opt
sudo rm -rf xiaoli 2>/dev/null || true
sudo git clone https://github.com/hongyue76/xiaoli.git
cd xiaoli/backend
echo "开始构建（约15-30分钟）..."
./mvnw clean package -DskipTests -q

# 创建启动脚本
mkdir -p /opt/xiaoli/logs
cat > /opt/xiaoli/start.sh << 'START'
#!/bin/bash
cd /opt/xiaoli/backend
nohup java -Xms256m -Xmx512m -jar ms-consult/target/ms-consult-1.0.0.jar \
  --server.port=18081 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/xiaoli_legal \
  --spring.datasource.username=xiaoli \
  --spring.datasource.password=Xiaoli@2024 \
  --spring.data.redis.host=localhost \
  --spring.data.redis.port=6379 \
  > /opt/xiaoli/logs/consult.log 2>&1 &
echo "服务已启动!"
START
chmod +x /opt/xiaoli/start.sh

echo ""
echo "=========================================="
echo "  部署完成!"
echo "=========================================="
echo "启动服务: /opt/xiaoli/start.sh"
echo "查看日志: tail -f /opt/xiaoli/logs/consult.log"
echo "健康检查: curl http://localhost:18081/actuator/health"
