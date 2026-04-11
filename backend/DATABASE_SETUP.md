# 数据库配置指南

## PostgreSQL安装和配置

### Windows安装

1. 下载PostgreSQL: https://www.postgresql.org/download/windows/
2. 安装时记住设置的密码（推荐：`Xiaoli@2024`）
3. 安装完成后，配置环境变量

### 启动PostgreSQL服务

**方法1：使用Windows服务**
```bash
net start postgresql-x64-15
```

**方法2：手动启动**
- 打开pgAdmin
- 连接到本地PostgreSQL服务器
- 确保服务正在运行

### 初始化数据库

1. 打开命令行工具（psql或pgAdmin的查询工具）
2. 执行初始化脚本：

```bash
# 使用psql命令行
psql -U postgres -f init-database.sql

# 或在pgAdmin中打开查询工具，复制粘贴init-database.sql内容并执行
```

3. 验证数据库创建成功：
```sql
\l  -- 列出所有数据库
\c xiaoli_legal  -- 切换到xiaoli_legal数据库
\dt  -- 列出所有表
```

## Redis安装和配置

### Windows安装

1. 下载Redis for Windows: https://github.com/microsoftarchive/redis/releases
2. 解压到指定目录（如：`C:\redis`）
3. 配置密码（可选）

### 启动Redis服务

**方法1：使用redis-server.exe**
```bash
cd C:\redis
redis-server.exe
```

**方法2：使用redis.windows.conf配置文件**
```bash
cd C:\redis
redis-server.exe redis.windows.conf
```

**方法3：注册为Windows服务**
```bash
cd C:\redis
redis-server.exe --service-install redis.windows.conf
redis-server.exe --service-start
```

### 验证Redis运行

```bash
# 打开redis-cli
redis-cli

# 如果设置了密码
redis-cli -a Xiaoli@2024

# 测试连接
ping
# 应该返回: PONG
```

## 验证服务状态

### 检查PostgreSQL
```bash
netstat -ano | findstr :5432
```

### 检查Redis
```bash
netstat -ano | findstr :6379
```

如果看到对应的端口在监听，说明服务运行正常。

## 环境变量配置

如果需要，可以设置以下环境变量（可选）：

**PostgreSQL:**
```bash
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=xiaoli_legal
set DB_USERNAME=xiaoli
set DB_PASSWORD=Xiaoli@2024
```

**Redis:**
```bash
set REDIS_HOST=localhost
set REDIS_PORT=6379
set REDIS_PASSWORD=Xiaoli@2024
```

**腾讯元器API（AI对话需要）:**
```bash
set TENCENT_YUANQI_BASE_URL=https://api.yuanqi.tencent.com
set TENCENT_YUANQI_ASSISTANT_ID=your-assistant-id
set TENCENT_YUANQI_TOKEN=your-token
```

## 常见问题

### Q: PostgreSQL服务无法启动
A: 检查端口5432是否被占用，修改配置文件中的端口号

### Q: Redis连接失败
A: 确认redis-server正在运行，检查防火墙设置

### Q: 数据库权限错误
A: 使用postgres用户执行初始化脚本，确保创建了xiaoli用户并授权

### Q: 忘记数据库密码
A: 重置PostgreSQL密码：
```sql
ALTER USER xiaoli WITH PASSWORD 'new_password';
```

## Docker方式（推荐开发环境）

如果不想手动安装，可以使用Docker：

```bash
# 启动PostgreSQL
docker run -d --name xiaoli-postgres \
  -e POSTGRES_DB=xiaoli_legal \
  -e POSTGRES_USER=xiaoli \
  -e POSTGRES_PASSWORD=Xiaoli@2024 \
  -p 5432:5432 \
  postgres:15

# 启动Redis
docker run -d --name xiaoli-redis \
  -e REDIS_PASSWORD=Xiaoli@2024 \
  -p 6379:6379 \
  redis:7-alpine redis-server --requirepass Xiaoli@2024

# 初始化数据库
docker exec -i xiaoli-postgres psql -U xiaoli -d xiaoli_legal < init-database.sql
```

## 启动后端服务

配置好数据库后，运行：

```bash
cd d:/me/project/xiaoli/backend
start-all-services.bat
```
