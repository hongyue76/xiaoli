# Docker方式启动指南

由于Docker需要管理员权限，请按以下步骤操作：

## 方法1：使用Docker（需要管理员权限）

### 步骤1：以管理员身份运行脚本

1. 找到文件：`backend/start-docker-databases.bat`
2. 右键点击该文件
3. 选择"以管理员身份运行"
4. 等待数据库启动完成

### 步骤2：启动后端服务

在新的命令行窗口中运行：
```bash
cd d:/me/project/xiaoli/backend
start-all-services.bat
```

### 步骤3：启动前端

```bash
cd d:/me/project/xiaoli/frontend/web
npm run dev
```

---

## 方法2：手动安装PostgreSQL和Redis（无需Docker）

### 安装PostgreSQL

1. **下载PostgreSQL**
   - 访问：https://www.postgresql.org/download/windows/
   - 下载安装包（推荐15版本）

2. **安装PostgreSQL**
   - 运行安装程序
   - 设置密码为：`Xiaoli@2024`
   - 选择默认端口：`5432`
   - 完成安装

3. **初始化数据库**
   - 打开pgAdmin或命令行工具
   - 执行脚本：`backend/init-database.sql`

### 安装Redis

1. **下载Redis for Windows**
   - 访问：https://github.com/tporadowski/redis/releases
   - 下载最新版本的zip文件

2. **解压并运行**
   - 解压到某个目录（如 `C:\redis`）
   - 打开命令行，进入该目录
   - 运行：`redis-server.exe`

### 启动服务

```bash
# 启动后端
cd d:/me/project/xiaoli/backend
start-all-services.bat

# 启动前端
cd d:/me/project/xiaoli/frontend/web
npm run dev
```

---

## 方法3：使用纯前端模式（最快，推荐）

如果只是测试或演示，不需要后端数据库：

```bash
cd d:/me/project/xiaoli/frontend/web
npm run dev
```

访问 http://localhost:3000

**优点：**
- 无需安装任何依赖
- 无需启动数据库
- 使用内置静态数据
- AI对话有预设回复

---

## 验证服务状态

启动后，检查端口：

```bash
# 检查PostgreSQL
netstat -ano | findstr :5432

# 检查Redis
netstat -ano | findstr :6379

# 检查后端服务
netstat -ano | findstr "8081 8082 8083"

# 检查前端
netstat -ano | findstr :3000
```

---

## 推荐方案

**新手/快速演示** → 使用方法3（纯前端模式）
**开发/测试** → 使用方法1（Docker方式）
**生产环境** → 使用方法2（手动安装）

---

## 故障排除

### Docker无法启动
- 确保Docker Desktop已安装并运行
- 以管理员权限运行脚本
- 检查Docker服务状态

### PostgreSQL连接失败
- 检查PostgreSQL服务是否运行
- 确认端口5432未被占用
- 验证用户名密码：xiaoli / Xiaoli@2024

### Redis连接失败
- 检查redis-server是否运行
- 确认端口6379未被占用

### 后端服务启动失败
- 确保PostgreSQL和Redis已启动
- 检查日志文件查看错误信息
- 确认Java版本（需要JDK 17+）
