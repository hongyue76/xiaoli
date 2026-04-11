# DeepSeek API 意图路由集成指南

## 快速开始

### 1. 后端配置

在 `backend/intent-core/src/main/resources/application.properties` 中配置：

```properties
# 设置你的 DeepSeek API Key
deepseek.api.key=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# 可选：自定义 DeepSeek API 基础 URL
deepseek.api.base-url=https://api.deepseek.com/v1

# 可选：调整意图识别的置信度阈值
intent.confidence-threshold=0.6
```

### 2. 启动服务

```bash
# 编译项目
cd backend
mvn clean compile

# 启动意图路由服务
cd intent-core
mvn spring-boot:run

# 服务将在 http://localhost:8087/intent-router 启动
```

### 3. 前端集成

在 React 组件中使用：

```typescript
import { intentRouterAPI } from '@/services/api';

// 分析意图
const intent = await intentRouterAPI.analyzeIntent("劳动合同法规定试用期多长？");

// 智能问答
const response = await intentRouterAPI.chatWithRetrieval({
  question: "劳动合同法规定试用期多长？",
  userId: 1,
  sessionId: "session_123"
});
```

## API 详解

### 意图分析 API

**接口**: `POST /intent-router/analyze`

**功能**: 分析用户问题的意图类型

**请求示例**:
```json
{
  "question": "劳动合同法规定试用期多长？"
}
```

**响应示例**:
```json
{
  "success": true,
  "intent": {
    "type": "PROFESSIONAL",
    "needSearch": true,
    "confidence": 0.92,
    "reason": "问题涉及具体法律条文规定",
    "originalQuestion": "劳动合同法规定试用期多长？"
  }
}
```

**意图类型说明**:

| 类型 | 说明 | 是否检索 |
|-----|------|---------|
| PROFESSIONAL | 专业法律问题 | 是 |
| GENERAL | 通用问题 | 否 |
| CHAT | 日常闲聊 | 否 |
| WEATHER | 天气查询 | 否 |
| SUMMARY | 内容总结 | 否 |
| AMBIGUOUS | 意图不明确 | 否 |

### 智能问答 API

**接口**: `POST /intent-router/chat`

**功能**: 根据意图自动路由，提供智能回答

**请求示例**:
```json
{
  "question": "劳动合同法规定试用期多长？",
  "userId": 1,
  "sessionId": "session_123"
}
```

**响应示例**:
```json
{
  "success": true,
  "answer": "根据《劳动合同法》第十九条规定...",
  "intentType": "PROFESSIONAL",
  "processType": "PROFESSIONAL_WITH_SEARCH",
  "searchResults": "[{\"title\":\"劳动合同法第十九条\",\"content\":\"...\"}]",
  "duration": 1250
}
```

**处理类型说明**:

| 类型 | 说明 | API 调用 |
|-----|------|---------|
| PROFESSIONAL_WITH_SEARCH | 专业问题+检索 | 得理API + DeepSeek |
| GENERAL_WITHOUT_SEARCH | 通用问题 | DeepSeek |

## 组件使用

### IntentAwareAIChat 组件

完整的智能聊天组件，支持意图显示和处理流程可视化。

```typescript
import { IntentAwareAIChat } from '@/components/AIChat';

export default function ChatPage() {
  return (
    <div className="chat-page">
      <IntentAwareAIChat />
    </div>
  );
}
```

**功能特性**:
- ✅ 实时意图识别和显示
- ✅ 处理流程可视化
- ✅ 检索结果展示
- ✅ 消息格式化（Markdown）
- ✅ 自动滚动
- ✅ 加载状态
- ✅ 空状态提示

## 自定义配置

### 1. 调整意图识别 Prompt

在 `IntentRouterService.java` 中修改 Prompt：

```java
private static final String INTENT_RECOGNITION_PROMPT = """
你是一个专业的意图识别助手。请分析用户的问题属于哪种意图类型。

意图类型说明：
1. PROFESSIONAL - 专业法律问题：涉及具体法条、案例、法规等法律专业知识
2. GENERAL - 通用问题：常识性、百科类知识问题
3. CHAT - 日常闲聊：打招呼、情感表达、日常对话
4. WEATHER - 天气查询：询问天气、温度、降雨等
5. SUMMARY - 内容总结：要求总结、提炼、概括
6. AMBIGUOUS - 意图不明确：问题过于简短或含义模糊

只返回JSON格式，不要有其他内容。
""";
```

### 2. 配置置信度阈值

在 `application.properties` 中：

```properties
# 调整意图识别的置信度阈值（0-1）
intent.confidence-threshold=0.7
```

### 3. 启用/禁用降级

```properties
# 启用降级方案（当 DeepSeek 调用失败时的处理）
intent.enable-fallback=true
intent.fallback-intent=GENERAL
```

## 测试

### 单元测试

```bash
cd backend/intent-core
mvn test
```

### 手动测试

使用 Postman 或 curl 测试：

```bash
# 测试意图分析
curl -X POST http://localhost:8087/intent-router/analyze \
  -H "Content-Type: application/json" \
  -d '{"question":"劳动合同法规定试用期多长？"}'

# 测试智能问答
curl -X POST http://localhost:8087/intent-router/chat \
  -H "Content-Type: application/json" \
  -d '{"question":"劳动合同法规定试用期多长？","userId":1}'
```

### 演示页面

访问演示页面：`http://localhost:5173/intent-router-demo`

## 常见问题

### Q1: 如何获取 DeepSeek API Key？

A: 访问 [DeepSeek 官网](https://platform.deepseek.com/) 注册账号并获取 API Key。

### Q2: 意图识别不准确怎么办？

A: 
1. 调整 `confidence-threshold` 提高识别准确率
2. 优化 Prompt 提供更多示例
3. 收集标注数据训练微调模型

### Q3: 如何处理 API 调用超时？

A: 在 `application.properties` 中调整：

```properties
deepseek.api.timeout=60000  # 增加超时时间到60秒
deepseek.api.max-retries=5  # 增加重试次数
```

### Q4: 如何集成到现有系统？

A: 
1. 将 `intent-core` 模块添加到你的项目
2. 配置 `application.properties`
3. 通过 HTTP API 调用接口
4. 或直接注入 `IntentRouterService` 使用

## 监控和日志

### 日志级别配置

```properties
# 调试模式
logging.level.com.xiaoli.intent=DEBUG

# 生产模式
logging.level.com.xiaoli.intent=INFO
```

### 关键日志

```
意图识别: PROFESSIONAL, 置信度: 0.92, 耗时: 150ms
路由决策: PROFESSIONAL_WITH_SEARCH, 检索结果: 5条, 总耗时: 1200ms
```

## 性能优化建议

1. **启用缓存**: 对相同的意图分析结果进行缓存
2. **异步处理**: 使用 `@Async` 异步调用 DeepSeek API
3. **连接池**: 配置 OkHttp 连接池
4. **限流**: 使用 `@RateLimiter` 限制请求频率

## 更新日志

### v1.0.0 (2024-03-30)
- ✅ 初始版本发布
- ✅ 支持 6 种意图类型识别
- ✅ 集成得理法搜 API
- ✅ 前端 React 组件
- ✅ 完整的 API 文档

## 支持

如有问题，请提交 Issue 或联系开发团队。
