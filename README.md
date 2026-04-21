# 律法先锋 - 法律AI智能平台

> 🔗 **在线访问**: http://101.42.47.231  
> 📦 **GitHub**: https://github.com/hongyue76/xiaoli

---

## 功能特性

| 功能 | 说明 |
|------|------|
| 💬 法律咨询 | AI智能问答，自动检索相似案例 |
| 📋 案例检索 | 基于得理法搜的真实案例库 |
| 📄 文书生成 | 起诉状、答辩状等文书智能生成 |
| 📝 合同审查 | 风险识别、条款合规性检查 |
| ⚖️ 司法决策 | 量刑建议、审判预测 |

> ⚠️ **免责声明**: 本平台提供的所有服务仅供参考，不构成正式法律意见

---

## 技术栈

**后端**: Spring Boot 3.x | PostgreSQL | Redis | DeepSeek AI  
**前端**: React 18 + TypeScript + Vite + Ant Design  
**AI**: DeepSeek API + 得理法搜 API

---

## 项目结构

```
xiaoli/
├── backend/           # 后端微服务
│   ├── ms-consult/   # 法律咨询 (18081)
│   ├── ms-document/  # 文书服务 (18082)
│   └── ms-caseinfo/  # 案例检索 (18083)
├── frontend/web/      # 前端应用
└── deployment/        # 部署配置
```

---

## 快速开始

### 1. 启动后端服务

```bash
cd backend
mvn clean package -DskipTests

# 启动服务
java -jar ms-consult/target/ms-consult-1.0.0.jar --server.port=18081
```

### 2. 启动前端

```bash
cd frontend/web
npm install
npm run dev
```

### 3. 访问系统

- 前端: http://localhost:3000
- API文档: http://localhost:18081/swagger-ui.html

---

## API接口

### 法律咨询

```bash
curl -X POST http://localhost:18081/api/consult/chat \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"劳动合同纠纷怎么处理"}]}'
```

### 案例检索

```bash
curl -X POST http://localhost:18083/api/case/search \
  -H "Content-Type: application/json" \
  -d '{"keyword":"劳动纠纷","page":1,"pageSize":10}'
```

---

## 配置说明

### 数据库

```sql
CREATE DATABASE xiaoli_legal;
CREATE USER xiaoli WITH PASSWORD 'Xiaoli@2024';
```

初始化表结构：
```bash
psql -U xiaoli -d xiaoli_legal -f backend/init-database.sql
```

### API Key配置

在 `application.yml` 中配置：

```yaml
deepseek:
  api:
    key: your-deepseek-api-key

delilegal:
  api-key: your-delilegal-api-key
```

---

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| ms-consult | 18081 | 法律咨询 |
| ms-document | 18082 | 文书生成 |
| ms-caseinfo | 18083 | 案例检索 |
| PostgreSQL | 5432 | 数据库 |
| Redis | 6379 | 缓存 |

---

## 部署指南

### 服务器部署

1. 安装依赖: JDK 17, Node.js 18, PostgreSQL, Redis, Nginx
2. 配置数据库并导入 init-database.sql
3. 构建后端: `mvn clean package -DskipTests`
4. 部署前端到 /var/www/html
5. 配置nginx反向代理

详细步骤见 [demo/SERVER_DEMO.md](./demo/SERVER_DEMO.md)

---

## 商标声明

| 商标 | 所有者 |
|------|--------|
| 得理法搜/得理AI | 得理科技(深圳)有限公司 |
| DeepSeek | 深度求索(杭州)人工智能科技有限公司 |
| 腾讯/腾讯云 | 深圳市腾讯计算机系统有限公司 |
| Ant Design | 蚂蚁集团 |
| Spring Boot | VMware, Inc. |
| React | Meta Platforms, Inc. |

---

## 许可证

MIT License - 详见 [LICENSE](./LICENSE)
