@echo off
chcp 65001 >nul
echo ==========================================
echo   律法先锋 - Ubuntu 服务器一键部署
echo ==========================================
echo.

set SERVER=101.42.47.231
set USER=root

echo [步骤1] 检查SSH连接...
echo 请在提示时输入密码（如有）
echo.

echo [步骤2] 创建部署脚本到服务器...
echo 此命令将创建部署脚本

ssh %USER%@%SERVER% "bash -c 'cat > /tmp/deploy.sh << '\''EOF'\''
#!/bin/bash
set -e
echo ==========================================
echo   律法先锋 一键部署
echo ==========================================

echo [1/6] 更新系统并安装依赖...
apt update -qq
apt install -y -qq openjdk-17-jdk maven postgresql postgresql-contrib redis-server nginx git curl

echo [2/6] 验证安装...
java -version 2>&1 | head -1
mvn -version 2>&1 | head -1

echo [3/6] 配置 PostgreSQL...
systemctl start postgresql
systemctl enable postgresql
sudo -u postgres psql -c \"CREATE DATABASE xiaoli_legal;\" 2>/dev/null || true
sudo -u postgres psql -c \"CREATE USER xiaoli WITH PASSWORD ''''Xiaoli@2024'''';\" 2>/dev/null || true
sudo -u postgres psql -c \"GRANT ALL PRIVILEGES ON DATABASE xiaoli_legal TO xiaoli;\" 2>/dev/null || true

echo [4/6] 配置 Redis...
systemctl start redis-server
systemctl enable redis-server

echo [5/6] 克隆并构建项目...
cd /opt
rm -rf xiaoli 2>/dev/null || true
git clone https://github.com/hongyue76/xiaoli.git
cd xiaoli/backend
echo 开始构建（约15-30分钟）...
./mvnw clean package -DskipTests -q

echo [6/6] 创建启动脚本...
mkdir -p /opt/xiaoli/logs
cat > /opt/xiaoli/start.sh << ''''START''''
#!/bin/bash
cd /opt/xiaoli/backend
nohup java -Xms256m -Xmx512m -jar ms-consult/target/ms-consult-1.0.0.jar --server.port=18081 --spring.datasource.url=jdbc:postgresql://localhost:5432/xiaoli_legal --spring.datasource.username=xiaoli --spring.datasource.password=Xiaoli@2024 --spring.data.redis.host=localhost --spring.data.redis.port=6379 > /opt/xiaoli/logs/consult.log 2>&1 &
echo 服务已启动!
START''''
chmod +x /opt/xiaoli/start.sh

echo.
echo ==========================================
echo   部署完成！
echo ==========================================
echo 启动服务: /opt/xiaoli/start.sh
echo 查看日志: tail -f /opt/xiaoli/logs/consult.log
echo 健康检查: curl http://localhost:18081/actuator/health
echo 访问地址: http://%SERVER%:18081
EOF
chmod +x /tmp/deploy.sh'"

echo.
echo [步骤3] 执行部署脚本...
ssh %USER%@%SERVER% "bash /tmp/deploy.sh"

echo.
echo ==========================================
echo   部署命令执行完成！
echo ==========================================
echo.
echo 如果构建成功，现在可以：
echo 1. SSH 登录服务器: ssh %USER%@%SERVER%
echo 2. 启动服务: /opt/xiaoli/start.sh
echo 3. 查看日志: tail -f /opt/xiaoli/logs/consult.log
echo.
pause
