# 律法先锋 - Ubuntu 服务器部署指南

## 服务器信息
- IP: 101.42.47.231
- SSH 端口: 22 (默认)

---

## 方式一：一键部署（推荐）

在你的本地终端执行以下命令：

```bash
# 1. SSH 登录服务器
ssh root@101.42.47.231

# 2. 在服务器上创建部署脚本
cat > /tmp/deploy.sh << 'DEPLSCRIPT'
#!/bin/bash
set -e
echo "=========================================="
echo "  律法先锋部署开始..."
echo "=========================================="

# 安装 Java 17
apt update && apt install -y openjdk-17-jdk maven postgresql postgresql-contrib redis-server nginx

# 配置 PostgreSQL
systemctl start postgresql
systemctl enable postgresql
sudo -u postgres psql -c "CREATE DATABASE xiaoli_legal;"
sudo -u postgres psql -c "CREATE USER xiaoli WITH PASSWORD 'Xiaoli@2024';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE xiaoli_legal TO xiaoli;"

# 配置 Redis
systemctl start redis-server
systemctl enable redis-server

# 克隆项目
cd /opt
git clone https://github.com/hongyue76/xiaoli.git
cd xiaoli/backend

# 构建项目
./mvnw clean package -DskipTests

# 创建启动脚本
mkdir -p /opt/xiaoli/logs
cat > /opt/xiaoli/start.sh << 'START'
#!/bin/bash
cd /opt/xiaoli/backend
nohup java -jar ms-consult/target/ms-consult-1.0.0.jar \
  --server.port=18081 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/xiaoli_legal \
  --spring.datasource.username=xiaoli \
  --spring.datasource.password=Xiaoli@2024 \
  > /opt/xiaoli/logs/consult.log 2>&1 &
echo "服务已启动，PID: $!"
START
chmod +x /opt/xiaoli/start.sh

echo "=========================================="
echo "  部署完成!"
echo "=========================================="
echo "启动服务: /opt/xiaoli/start.sh"
echo "查看日志: tail -f /opt/xiaoli/logs/consult.log"
echo "健康检查: curl http://localhost:18081/actuator/health"
DEPLSCRIPT

# 3. 执行部署脚本
chmod +x /tmp/deploy.sh
bash /tmp/deploy.sh
```

---

## 方式二：分步执行

如果一键部署失败，按以下步骤操作：

### 步骤 1: SSH 登录
```bash
ssh root@101.42.47.231
```

### 步骤 2: 安装依赖
```bash
apt update
apt install -y openjdk-17-jdk maven postgresql postgresql-contrib redis-server nginx git
java -version  # 验证 Java
mvn -version   # 验证 Maven
```

### 步骤 3: 配置 PostgreSQL
```bash
systemctl start postgresql
systemctl enable postgresql

# 创建数据库
sudo -u postgres psql << EOF
CREATE DATABASE xiaoli_legal;
CREATE USER xiaoli WITH PASSWORD 'Xiaoli@2024';
GRANT ALL PRIVILEGES ON DATABASE xiaoli_legal TO xiaoli;
EOF
```

### 步骤 4: 配置 Redis
```bash
systemctl start redis-server
systemctl enable redis-server
redis-cli ping  # 应返回 PONG
```

### 步骤 5: 克隆项目
```bash
cd /opt
git clone https://github.com/hongyue76/xiaoli.git
cd xiaoli/backend
```

### 步骤 6: 构建项目
```bash
./mvnw clean package -DskipTests
```
⚠️ **这可能需要 15-30 分钟**

### 步骤 7: 启动服务
```bash
mkdir -p /opt/xiaoli/logs

# 启动咨询微服务
nohup java -Xms256m -Xmx512m \
  -jar ms-consult/target/ms-consult-1.0.0.jar \
  --server.port=18081 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/xiaoli_legal \
  --spring.datasource.username=xiaoli \
  --spring.datasource.password=Xiaoli@2024 \
  --spring.data.redis.host=localhost \
  --spring.data.redis.port=6379 \
  > /opt/xiaoli/logs/consult.log 2>&1 &

echo "服务 PID: $!"
```

### 步骤 8: 验证部署
```bash
# 查看日志
tail -f /opt/xiaoli/logs/consult.log

# 健康检查
curl http://localhost:18081/actuator/health

# 检查进程
ps aux | grep ms-consult
```

---

## 防火墙配置

```bash
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw allow 18081/tcp # 微服务
```

---

## 常用命令

| 操作 | 命令 |
|------|------|
| 启动 | `/opt/xiaoli/start.sh` |
| 停止 | `pkill -f ms-consult` |
| 重启 | `pkill -f ms-consult && /opt/xiaoli/start.sh` |
| 日志 | `tail -f /opt/xiaoli/logs/consult.log` |
| 状态 | `curl http://localhost:18081/actuator/health` |
