# 数据库设置说明

## 方式一：使用预置数据（推荐）

项目已包含预置数据库备份，可以快速恢复数据。

### 1. 复制数据库备份
将 `deployment/database_backup.sql` 复制到他人电脑的 `deployment/` 目录。

### 2. 启动 Docker 数据库
```bash
cd deployment
docker-compose up -d postgres redis
```

### 3. 恢复数据
```bash
docker exec -i xiaoli-postgres psql -U xiaoli -d xiaoli_legal < database_backup.sql
```

## 方式二：使用 Docker Volume 备份

### 导出当前数据库
```bash
docker exec xiaoli-postgres pg_dump -U xiaoli xiaoli_legal > database_backup.sql
```

### 恢复数据
```bash
docker exec -i xiaoli-postgres psql -U xiaoli -d xiaoli_legal < database_backup.sql
```

## 方式三：完全手动初始化

如果没有使用备份，可以手动运行初始化脚本：

```bash
# 进入 backend 目录
cd backend

# 启动数据库
docker run -d --name xiaoli-postgres \
  -e POSTGRES_DB=xiaoli_legal \
  -e POSTGRES_USER=xiaoli \
  -e POSTGRES_PASSWORD=Xiaoli@2024 \
  -p 5432:5432 \
  postgres:15

# 等待 10 秒
timeout /t 10

# 初始化数据库
docker exec -i xiaoli-postgres psql -U xiaoli -d xiaoli_legal < init-database.sql
```

## 数据库连接信息

| 项目 | 值 |
|------|-----|
| 主机 | localhost |
| 端口 | 5432 |
| 数据库名 | xiaoli_legal |
| 用户名 | xiaoli |
| 密码 | Xiaoli@2024 |
