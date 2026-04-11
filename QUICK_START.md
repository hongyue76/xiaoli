# 律法先锋 - 快速启动指南

## 最快启动方式（推荐测试/演示）

### 方案1：纯前端模式（无需后端）

```bash
cd d:/me/project/xiaoli/frontend/web
npm run dev
```

访问：http://localhost:3000

**特点：**
- ✅ 无需安装任何依赖
- ✅ 无需启动后端服务
- ✅ 使用内置静态数据
- ✅ AI对话有预设回复
- ⚠️ 数据不会持久化

---

### 方案2：完整后端模式（需要数据库）

#### 前置要求

1. **安装PostgreSQL** (端口5432)
   - 下载：https://www.postgresql.org/download/windows/
   - 默认密码：`Xiaoli@2024`

2. **安装Redis** (端口6379)
   - 下载：https://github.com/microsoftarchive/redis/releases
   - 可选密码：`Xiaoli@2024`

#### 快速初始化（推荐使用Docker）

如果已安装Docker，一键启动所有依赖：

```bash
# 启动PostgreSQL
docker run -d --name xiaoli-postgres ^
  -e POSTGRES_DB=xiaoli_legal ^
  -e POSTGRES_USER=xiaoli ^
  -e POSTGRES_PASSWORD=Xiaoli@2024 ^
  -p 5432:5432 ^
  postgres:15

# 启动Redis
docker run -d --name xiaoli-redis ^
  -e REDIS_PASSWORD=Xiaoli@2024 ^
  -p 6379:6379 ^
  redis:7-alpine redis-server --requirepass Xiaoli@2024

# 初始化数据库
docker exec -i xiaoli-postgres psql -U xiaoli -d xiaoli_legal < backend/init-database.sql
```

#### 或手动初始化

1. 启动PostgreSQL和Redis
2. 执行数据库初始化脚本：
   ```bash
   psql -U postgres -f backend/init-database.sql
   ```

#### 启动服务

```bash
# 1. 启动后端（会自动检查PostgreSQL和Redis）
cd d:/me/project/xiaoli/backend
start-all-services.bat

# 2. 启动前端
cd d:/me/project/xiaoli/frontend/web
npm run dev
```

**特点：**
- ✅ 完整功能
- ✅ 数据持久化
- ✅ 真实AI对话（需配置腾讯元器API）
- ⚠️ 需要配置数据库

---

## 服务端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端 | 3000 | Vite开发服务器 |
| PostgreSQL | 5432 | 数据库 |
| Redis | 6379 | 缓存 |
| ms-consult | 8081 | 法律咨询/AI对话 |
| ms-document | 8082 | 文书生成 |
| ms-caseinfo | 8083 | 案例检索 |

---

## 验证服务状态

### 检查端口
```bash
netstat -ano | findstr "3000 5432 6379 8081 8082 8083"
```

### 测试API
```bash
# 测试后端健康检查
curl http://localhost:8081/health
curl http://localhost:8083/case/hot?limit=5
```

---

## 停止服务

```bash
# 停止后端
cd d:/me/project/xiaoli/backend
stop-all-services.bat

# 停止前端（在前端命令行按 Ctrl+C）

# 停止Docker容器
docker stop xiaoli-postgres xiaoli-redis
```

---

## 配置AI对话功能（可选）

如果需要真实的AI对话能力，需要配置腾讯元器API：

1. 申请腾讯元器智能体账号：https://yuanqi.tencent.com
2. 获取assistant-id和token
3. 设置环境变量：
   ```bash
   set TENCENT_YUANQI_BASE_URL=https://api.yuanqi.tencent.com
   set TENCENT_YUANQI_ASSISTANT_ID=your-assistant-id
   set TENCENT_YUANQI_TOKEN=your-token
   ```
4. 重启ms-consult服务

---

## 常见问题

### Q: 前端无法访问后端API
A: 检查：
1. 后端服务是否启动
2. 端口是否正确（8081/8082/8083）
3. 查看浏览器控制台错误信息

### Q: 数据库连接失败
A: 检查：
1. PostgreSQL是否运行
2. 数据库xiaoli_legal是否创建
3. 用户名密码是否正确

### Q: AI对话无法使用
A:
1. 确认ms-consult服务已启动
2. 如需真实AI，配置腾讯元器API
3. 否则使用预设回复模式

### Q: Redis连接失败
A: 检查Redis是否运行，端口6379是否被占用

---

## 开发建议

**新手/演示：**
使用方案1（纯前端模式），无需配置

**开发/测试：**
使用Docker启动数据库，然后运行完整后端

**生产环境：**
使用独立的数据库服务器，配置完整的监控和日志

---

## 下一步

- 查看 `START_GUIDE.md` 了解详细功能说明
- 查看 `backend/DATABASE_SETUP.md` 了解数据库配置详情
- 查看 `README.md` 了解项目架构

祝使用愉快！🎉
