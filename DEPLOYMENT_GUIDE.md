# 律法先锋项目 - 部署和使用指南

## 项目概述

律法先锋是一个基于得理法搜AI构建的法律智能平台，提供案例检索、法规检索等功能。

## 已完成的工作

### 1. 后端API集成

✅ **已修改的文件**:
- `DelilegalServiceImpl.java` - 使用appid和secret进行Header鉴权
- `DelilegalProperties.java` - 添加appId和secret配置字段
- `application.yml` - 配置得理API参数
- `CaseController.java` - 添加POST接口支持

### 2. 前端界面集成

✅ **已修改的文件**:
- `api.ts` - 添加得理API调用方法
- `CaseSearch/index.tsx` - 添加Tab切换和数据源选择

### 3. 文档创建

✅ **已创建的文档**:
- `DELILEGAL_API_CONFIG.md` - API配置详细指南
- `DELILEGAL_INTEGRATION_SUMMARY.md` - 集成完成总结

## 快速开始

### 1. 环境要求

- JDK 17+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.6+

### 2. 配置得理API

#### 方法A: 使用环境变量（推荐）

创建 `.env` 文件：

```bash
# 得理AI配置
DELILEGAL_BASE_URL=https://openapi.delilegal.com
DELILEGAL_APP_ID=QthdBErlyaYvyXul
DELILEGAL_SECRET=EC5D455E6BD348CE8E18BE05926D2EBE
```

#### 方法B: 直接修改配置文件

编辑 `backend/ms-case/src/main/resources/application.yml`:

```yaml
delilegal:
  base-url: https://openapi.delilegal.com
  app-id: QthdBErlyaYvyXul
  secret: EC5D455E6BD348CE8E18BE05926D2EBE
  timeout: 30000
```

### 3. 启动后端服务

```bash
# 进入后端目录
cd backend/ms-case

# 编译项目
mvn clean package

# 启动服务
mvn spring-boot:run
```

服务将在 `http://localhost:8083` 启动

### 4. 启动前端服务

```bash
# 进入前端目录
cd frontend/web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 `http://localhost:3000` 启动

### 5. 访问应用

打开浏览器访问: `http://localhost:3000`

## 功能使用说明

### 案例检索

1. 进入"案例检索"页面
2. 选择数据源：
   - **本地数据库**: 使用项目内置的案例数据
   - **得理API**: 调用得理法搜实时检索
3. 选择检索方式：
   - **语义检索**: 基于AI语义理解
   - **关键词检索**: 基于关键词匹配
4. 输入关键词或长文本
5. 设置筛选条件（可选）：
   - 案件类型: 民事/刑事/行政/仲裁
   - 案件状态: 一审/二审/终审/再审
   - 法院层级: 最高院/高院/中院/基层院
   - 文书类型: 判决书/裁定书
6. 点击"搜索"按钮

### 法规检索

1. 进入"案例检索"页面
2. 切换到"法规检索"标签
3. 输入关键词
4. 选择检索方式：
   - **title**: 标题检索
   - **semantic**: 语义检索
5. 点击"搜索"按钮

### 查看详情

点击案例或法规标题可查看详细信息，包括：
- 案例：案号、法院、法官、裁判日期、案情简介、争议焦点、裁判要旨、裁判结果、法律依据
- 法规：标题、发布日期、生效日期、来源、完整内容

## API接口说明

### 后端接口

#### 案例检索（得理API）

**接口**: `POST /api/case/delilegal/search`

**请求体**:
```json
{
  "keywords": ["劳动合同", "经济补偿"],
  "longText": "关于解除劳动合同的赔偿问题",
  "courtLevelArr": ["中院"],
  "judgementTypeArr": ["判决书"]
}
```

#### 法规检索（得理API）

**接口**: `POST /api/case/delilegal/law`

**请求体**:
```json
{
  "keywords": ["劳动合同法"],
  "fieldName": "title"
}
```

### 前端API调用

```typescript
import { caseAPI } from '@/services/api';

// 案例检索
const result = await caseAPI.searchByDelilegal({
  keywords: ['劳动合同'],
  courtLevelArr: ['中院'],
  judgementTypeArr: ['判决书']
});

// 法规检索
const result = await caseAPI.searchLaws({
  keywords: ['劳动合同法'],
  fieldName: 'title'
});
```

## 常见问题

### Q1: 搜索结果为空

**原因**: 关键词太具体或测试库数据有限

**解决**: 尝试更通用的关键词，如"工伤"、"合同纠纷"

### Q2: 返回401/403错误

**原因**: appid或secret配置错误

**解决**:
- 检查配置文件中的appid和secret是否正确
- 确认API密钥是否已激活
- 联系得理客服: 0755-26907610

### Q3: 前端无法连接后端

**原因**: 后端服务未启动或端口被占用

**解决**:
- 确认后端服务已启动
- 检查8083端口是否被占用
- 查看浏览器控制台的网络请求日志

### Q4: 构建失败

**原因**: 依赖下载失败或版本不兼容

**解决**:
```bash
# 清理Maven缓存
mvn clean

# 强制更新依赖
mvn -U package

# 清理npm缓存
npm cache clean --force
```

## 性能优化建议

### 1. 启用缓存

在后端启用Redis缓存，减少API调用:

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600000 # 1小时
```

### 2. 限制API调用频率

添加请求限流，避免触发API限流:

```yaml
delilegal:
  rate-limit:
    enabled: true
    requests-per-minute: 60
```

### 3. 异步加载

前端使用分页加载，避免一次性加载大量数据:

```typescript
const [current, setCurrent] = useState(1);
const [pageSize] = useState(20);

const loadData = async (page: number) => {
  const result = await caseAPI.search({ current: page, size: pageSize });
  // ...
};
```

## 生产环境部署

### Docker部署

1. 构建镜像:

```bash
# 后端镜像
cd backend/ms-case
docker build -t xiaoli-case:latest .

# 前端镜像
cd frontend/web
docker build -t xiaoli-web:latest .
```

2. 使用Docker Compose:

```yaml
version: '3.8'
services:
  case-service:
    image: xiaoli-case:latest
    ports:
      - "8083:8083"
    environment:
      - DELILEGAL_BASE_URL=https://openapi.delilegal.com
      - DELILEGAL_APP_ID=${DELILEGAL_APP_ID}
      - DELILEGAL_SECRET=${DELILEGAL_SECRET}

  web-service:
    image: xiaoli-web:latest
    ports:
      - "3000:80"
    depends_on:
      - case-service
```

### Kubernetes部署

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: xiaoli-case
spec:
  replicas: 3
  selector:
    matchLabels:
      app: xiaoli-case
  template:
    metadata:
      labels:
        app: xiaoli-case
    spec:
      containers:
      - name: case-service
        image: xiaoli-case:latest
        ports:
        - containerPort: 8083
        env:
        - name: DELILEGAL_APP_ID
          valueFrom:
            secretKeyRef:
              name: delilegal-secrets
              key: app-id
        - name: DELILEGAL_SECRET
          valueFrom:
            secretKeyRef:
              name: delilegal-secrets
              key: secret
```

## 监控和日志

### 日志配置

```yaml
logging:
  level:
    com.xiaoli.legal: INFO
    com.xiaoli.legal.ai: DEBUG
  file:
    name: logs/xiaoli.log
    max-size: 100MB
    max-history: 30
```

### 监控指标

- API调用次数
- API响应时间
- 搜索成功率
- 错误率

## 联系方式

- **得理开放平台**: https://open.delilegal.com/
- **得理官方网站**: https://www.delilegal.com/
- **客服电话**: 0755-26907610

## 许可证

MIT License - 详见 [LICENSE](./LICENSE) 文件

---

**项目状态**: ✅ 可以正常运行并使用得理AI API

**最后更新**: 2026-03-27
