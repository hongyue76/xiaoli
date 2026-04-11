# 律法先锋 - 启动指南

## 前端启动

前端已经配置好代理，无需后端也能启动：

```bash
cd d:/me/project/xiaoli/frontend/web
npm run dev
```

访问：http://localhost:3000

## 后端启动

### 前置依赖

后端服务需要以下服务运行：

1. **PostgreSQL数据库**（端口5432）
   - 数据库名：xiaoli_legal
   - 用户名：xiaoli
   - 密码：Xiaoli@2024

2. **Redis缓存**（端口6379）
   - 密码：Xiaoli@2024

3. **腾讯元器智能体API**
   - 需要在环境变量中配置：
     - `TENCENT_YUANQI_BASE_URL`
     - `TENCENT_YUANQI_ASSISTANT_ID`
     - `TENCENT_YUANQI_TOKEN`

### 服务端口分配

| 服务名 | 端口 | 说明 | 启动脚本 |
|--------|------|------|----------|
| ms-consult | 8081 | 法律咨询/AI对话 | start-consult.bat |
| ms-document | 8082 | 法律文书生成 | start-document.bat |
| ms-caseinfo | 8083 | 案例检索 | start-case.bat |
| ms-contract | 8084 | 合同审查 | start-contract.bat |
| analysis | 8085 | 案件分析 | start-analysis.bat |
| ms-decision | 8086 | 决策辅助 | start-decision.bat |
| ms-compliance | 8087 | 企业合规 | start-compliance.bat |
| ms-evidence | 8088 | 证据分析 | start-evidence.bat |
| ms-speech | 8089 | 语音处理 | start-speech.bat |

### 启动方法

#### 方法1：单独启动某个服务

例如，只启动AI对话功能需要的服务：

```bash
cd d:/me/project/xiaoli/backend
start-consult.bat
```

#### 方法2：启动所有服务（需要完整依赖）

```bash
cd d:/me/project/xiaoli/backend
start-all-services.bat
```

### 快速测试（无后端模式）

如果不想启动后端，可以使用前端的静态数据：

1. 前端已经内置了静态示例数据
2. 点击搜索按钮会返回模拟数据
3. 所有功能都有降级处理

## 功能说明

### AI对话功能
- 需要启动：ms-consult服务（端口8081）
- 配置：腾讯元器智能体API
- 如果后端未启动，会显示错误消息

### 案例检索功能
- 需要启动：ms-caseinfo服务（端口8083）
- 支持本地数据库检索和得理API检索
- 有静态数据降级

### 其他功能
- 各功能对应不同的后端服务
- 前端会尝试调用后端API
- 如果失败会显示友好的错误提示

## 常见问题

### Q: AI无法回答问题
A: 需要启动ms-consult服务（8081端口），并配置腾讯元器API

### Q: 搜索功能不工作
A: 需要启动对应的后端服务，或使用静态数据模式

### Q: 如何只启动某个功能
A: 只启动对应的服务即可，例如AI对话只启动ms-consult

### Q: 数据库连接失败
A: 确保PostgreSQL和Redis正在运行，检查端口和密码配置

## 开发建议

对于快速测试和演示，建议：
1. 先启动前端
2. 测试静态数据模式
3. 根据需要启动对应的后端服务
4. 完整部署时再启动所有服务
