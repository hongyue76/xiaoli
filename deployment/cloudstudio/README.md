# 律法先锋 - CloudStudio 部署方案

## 架构说明

这是一个简化版的律法先锋后端，使用单个 Node.js 服务模拟 API，
适合快速部署和演示。完整 Java 微服务架构需要独立的服务器资源。

## 快速部署

### 方式一：使用 Docker（推荐）

```bash
# 1. 创建目录
mkdir -p /workspace/xiaoli && cd /workspace/xiaoli

# 2. 创建 docker-compose.yml（使用下方内容）
# 3. 启动服务
docker-compose up -d
```

### 方式二：直接运行 Node.js

```bash
# 1. 安装依赖
npm init -y && npm install express cors pg

# 2. 创建 server.js
# 3. 启动服务
node server.js
```

## API 端点

- 健康检查: GET http://localhost:3000/api/health
- 法律咨询: POST http://localhost:3000/api/consult/chat
- 案例检索: POST http://localhost:3000/api/case/search
- 法规检索: POST http://localhost:3000/api/case/delilegal/law
- 文书生成: POST http://localhost:3000/api/document/generate

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| PORT | 3000 | 服务端口 |
| DB_HOST | localhost | PostgreSQL 主机 |
| DB_PORT | 5432 | PostgreSQL 端口 |
| DB_NAME | xiaoli_legal | 数据库名 |
| DB_USER | xiaoli | 数据库用户 |
| DB_PASSWORD | Xiaoli@2024 | 数据库密码 |

## 公网访问配置

部署后需要：
1. 在 CloudStudio 控制台开放端口
2. 配置域名（可选）
3. 配置 HTTPS（可选）

## 完整版部署

如需完整 Java 微服务架构，请使用：
- 腾讯云服务器 CVM
- Docker Swarm 或 Kubernetes
- 需要 PostgreSQL + Redis + 多个微服务
