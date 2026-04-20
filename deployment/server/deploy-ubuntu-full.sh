#!/bin/bash
# ============================================
# 律法先锋 - Ubuntu 轻量应用服务器一键部署脚本
# 适用于腾讯云轻量应用服务器 (Ubuntu 22.04)
# ============================================

set -e

echo "=========================================="
echo "  律法先锋 - 完整部署脚本"
echo "=========================================="
echo "开始时间: $(date)"
echo ""

# ========== 配置 ==========
GIT_REPO="https://github.com/hongyue76/xiaoli.git"
INSTALL_DIR="/opt/xiaoli"
DB_NAME="xiaoli_legal"
DB_USER="xiaoli"
DB_PASSWORD="Xiaoli@2024"
REDIS_PASSWORD=""
LOG_DIR="/var/log/xiaoli"

# ========== 步骤1: 安装依赖 ==========
echo "[1/7] 安装系统依赖..."
apt update

# 安装 Java 17
apt install -y openjdk-17-jdk

# 验证 Java
java -version

# 安装 Maven
apt install -y maven

# 安装 PostgreSQL 15
apt install -y postgresql postgresql-contrib

# 安装 Redis
apt install -y redis-server

# 安装 Nginx
apt install -y nginx

# 安装 Docker (可选，用于容器化部署)
# apt install -y docker.io docker-compose

# ========== 步骤2: 配置 PostgreSQL ==========
echo "[2/7] 配置 PostgreSQL 数据库..."

# 启动 PostgreSQL
systemctl start postgresql
systemctl enable postgresql

# 创建数据库和用户
sudo -u postgres psql << EOF
-- 创建数据库
SELECT 'CREATE DATABASE $DB_NAME' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$DB_NAME')\gexec

-- 创建用户
DO
\$do\$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = '$DB_USER') THEN
      CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
   END IF;
END
\$do\$;

-- 授权
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
\c $DB_NAME
GRANT ALL ON SCHEMA public TO $DB_USER;
ALTER DATABASE $DB_NAME OWNER TO $DB_USER;
EOF

echo "PostgreSQL 配置完成"

# ========== 步骤3: 配置 Redis ==========
echo "[3/7] 配置 Redis..."

# 启动 Redis
systemctl start redis-server
systemctl enable redis-server

# 配置 Redis 密码 (可选)
if [ -n "$REDIS_PASSWORD" ]; then
    sed -i "s/# requirepass.*/requirepass $REDIS_PASSWORD/" /etc/redis/redis.conf
    systemctl restart redis-server
fi

echo "Redis 配置完成"

# ========== 步骤4: 克隆/更新项目 ==========
echo "[4/7] 克隆或更新项目..."

mkdir -p $INSTALL_DIR
cd $INSTALL_DIR

if [ -d ".git" ]; then
    echo "项目已存在，执行 git pull..."
    git pull origin main
else
    echo "克隆项目..."
    git clone $GIT_REPO .
fi

echo "项目代码已更新"

# ========== 步骤5: 构建后端 ==========
echo "[5/7] 构建后端 (Maven)..."
echo "这可能需要 10-30 分钟，请耐心等待..."

cd $INSTALL_DIR/backend

# 使用 Maven wrapper
chmod +x mvnw
./mvnw clean package -DskipTests -q

echo "后端构建完成!"

# ========== 步骤6: 配置启动脚本 ==========
echo "[6/7] 创建启动脚本..."

mkdir -p $LOG_DIR
mkdir -p $INSTALL_DIR/logs

# 创建主启动脚本
cat > $INSTALL_DIR/start-all.sh << 'STARTSCRIPT'
#!/bin/bash
# 律法先锋 - 启动所有服务

INSTALL_DIR="/opt/xiaoli"
LOG_DIR="/var/log/xiaoli"
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="xiaoli_legal"
DB_USER="xiaoli"
DB_PASSWORD="Xiaoli@2024"
REDIS_HOST="localhost"
REDIS_PORT="6379"

mkdir -p $LOG_DIR

start_service() {
    local name=$1
    local jar=$2
    local port=$3
    
    echo "启动 $name (端口 $port)..."
    
    if [ -f "$jar" ]; then
        nohup java -Xms256m -Xmx512m \
            -jar $jar \
            --server.port=$port \
            --spring.datasource.url=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME \
            --spring.datasource.username=$DB_USER \
            --spring.datasource.password=$DB_PASSWORD \
            --spring.data.redis.host=$REDIS_HOST \
            --spring.data.redis.port=$REDIS_PORT \
            > $LOG_DIR/$name.log 2>&1 &
        
        echo "$name PID: $!"
    else
        echo "警告: $jar 不存在，跳过"
    fi
}

cd $INSTALL_DIR/backend

# 启动各个微服务
start_service "ms-consult" "ms-consult/target/ms-consult-1.0.0.jar" "18081"
start_service "ms-case" "ms-case/target/ms-caseinfo-1.0.0.jar" "18083"
start_service "ms-document" "ms-document/target/ms-document-1.0.0.jar" "18082"

echo ""
echo "所有服务启动完成!"
echo "查看日志: tail -f $LOG_DIR/*.log"
STARTSCRIPT

chmod +x $INSTALL_DIR/start-all.sh

# 创建停止脚本
cat > $INSTALL_DIR/stop-all.sh << 'STOPSCRIPT'
#!/bin/bash
# 律法先锋 - 停止所有服务

echo "停止所有律法先锋服务..."

# 停止 Java 进程
pkill -f "ms-consult"
pkill -f "ms-caseinfo"
pkill -f "ms-document"

echo "所有服务已停止"
STOPSCRIPT

chmod +x $INSTALL_DIR/stop-all.sh

# 创建 systemd 服务
cat > /etc/systemd/system/xiaoli.service << 'SYSTEMD'
[Unit]
Description=律法先锋法律AI平台
After=postgresql.service redis-server.service
Wants=postgresql.service redis-server.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/xiaoli/backend
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /opt/xiaoli/backend/ms-consult/target/ms-consult-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
SYSTEMD

systemctl daemon-reload

echo "启动脚本创建完成"

# ========== 步骤7: 配置防火墙 ==========
echo "[7/7] 配置防火墙..."

# 开放端口
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw allow 18081/tcp # ms-consult
ufw allow 18082/tcp # ms-document
ufw allow 18083/tcp # ms-case

echo "防火墙配置完成"

# ========== 完成 ==========
echo ""
echo "=========================================="
echo "  部署完成!"
echo "=========================================="
echo ""
echo "启动服务:"
echo "  $INSTALL_DIR/start-all.sh"
echo ""
echo "或使用 systemd:"
echo "  systemctl start xiaoli"
echo "  systemctl enable xiaoli"
echo ""
echo "停止服务:"
echo "  $INSTALL_DIR/stop-all.sh"
echo ""
echo "查看日志:"
echo "  tail -f $LOG_DIR/ms-consult.log"
echo ""
echo "健康检查:"
echo "  curl http://localhost:18081/actuator/health"
echo ""
echo "完成时间: $(date)"
