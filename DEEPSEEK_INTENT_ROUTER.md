# DeepSeek 意图路由系统

## 概述

本系统基于 DeepSeek API 实现智能路由决策，自动区分专业法律问题和通用场景，提供最优化的处理策略。

## 系统架构

```
用户问题
    ↓
DeepSeek 意图识别
    ↓
┌───────────────┬─────────────────┐
│               │                 │
专业法律问题    通用问题         模糊问题
    ↓               ↓                 ↓
得理API检索    DeepSeek直接回答   询问澄清
    ↓
AI生成严谨回答
```

## 核心功能

### 1. 意图识别

通过 DeepSeek API 分析用户问题，识别出以下意图类型：

- **PROFESSIONAL** - 专业法律问题（需要精确法条）
- **GENERAL** - 通用问题（常识性问题）
- **CHAT** - 日常闲聊
- **WEATHER** - 天气查询
- **SUMMARY** - 内容总结
- **AMBIGUOUS** - 意图不明确

### 2. 智能路由

根据意图类型自动选择处理策略：

| 意图类型 | 处理策略 | API 调用 |
|---------|---------|---------|
| PROFESSIONAL | 专业模式 | 得理API + DeepSeek |
| GENERAL | 通用模式 | DeepSeek |
| CHAT | 闲聊模式 | DeepSeek |
| WEATHER | 天气模式 | 天气API |
| SUMMARY | 总结模式 | DeepSeek |
| AMBIGUOUS | 澄清模式 | 询问用户 |

## 后端实现

### 模块结构

```
backend/
├── common/common-ai/              # AI 公共模块
│   ├── config/
│   │   └── DeepSeekConfig.java   # DeepSeek API 配置
│   ├── model/
│   │   ├── Intent.java           # 意图枚举
│   │   ├── DeepSeekRequest.java  # DeepSeek 请求
│   │   └── DeepSeekResponse.java # DeepSeek 响应
│   └── service/
│       ├── DeepSeekService.java  # DeepSeek API 服务
│       └── IntentRouterService.java # 意图路由服务
└── intent-core/                   # 意图路由核心
    └── controller/
        └── IntentRouterController.java # 意图路由控制器
```

### API 接口

#### 1. 意图分析接口

**接口**: `POST /intent-router/analyze`

**请求**:
```json
{
  "question": "劳动合同法规定试用期多长？"
}
```

**响应**:
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

#### 2. 智能问答接口

**接口**: `POST /intent-router/chat`

**请求**:
```json
{
  "question": "劳动合同法规定试用期多长？",
  "userId": 1,
  "sessionId": "session_123"
}
```

**响应**:
```json
{
  "success": true,
  "answer": "根据《劳动合同法》第十九条规定...",
  "intentType": "PROFESSIONAL",
  "processType": "PROFESSIONAL_WITH_SEARCH",
  "searchResults": "...",
  "duration": 1250
}
```

### 配置文件

**application.properties**:
```properties
# DeepSeek API 配置
deepseek.api.key=your-api-key
deepseek.api.base-url=https://api.deepseek.com/v1
deepseek.api.model=deepseek-chat

# 意图识别配置
intent.confidence-threshold=0.6
intent.enable-fallback=true
```

## 前端实现

### 服务封装

**intentRouterService.ts**:
```typescript
// 分析用户意图
const intent = await intentRouterService.analyzeIntent(question);

// 带检索的智能问答
const response = await intentRouterService.routeQuestion(question);
```

### 组件集成

**IntentAwareAIChat.tsx**:
- 智能意图显示
- 处理流程可视化
- 检索结果展示
- 消息格式化

## 部署指南

### 1. 环境准备

```bash
# 克隆项目
git clone <repository>

# 进入后端目录
cd backend

# 配置环境变量
export DEEPSEEK_API_KEY=your-api-key
export DELILEGAL_API_BASE_URL=https://api.delilegal.com
```

### 2. 编译运行

```bash
# 编译项目
mvn clean compile

# 启动意图路由服务
cd intent-core
mvn spring-boot:run

# 服务将在 8087 端口启动
```

### 3. 健康检查

```bash
curl http://localhost:8087/intent-router/health
```

预期响应:
```json
{
  "status": "UP",
  "timestamp": "2024-03-30T10:00:00Z"
}
```

## 使用示例

### 后端调用示例

```java
@RestController
@RequestMapping("/intent-router")
public class IntentRouterController {

    @Autowired
    private IntentRouterService intentRouterService;

    @PostMapping("/analyze")
    public ResponseEntity<Intent> analyzeIntent(@RequestBody AnalyzeRequest request) {
        Intent intent = intentRouterService.analyzeIntent(request.getQuestion());
        return ResponseEntity.ok(intent);
    }
}
```

### 前端调用示例

```typescript
import { intentRouterAPI } from '@/services/api';

// 意图分析
const intent = await intentRouterAPI.analyzeIntent("劳动合同法规定试用期多长？");

// 智能问答
const response = await intentRouterAPI.chatWithRetrieval({
  question: "劳动合同法规定试用期多长？",
  userId: 1
});
```

## 示例场景

### 场景 1: 专业法律问题

**用户输入**: "劳动合同法规定试用期多长？"

**处理流程**:
1. DeepSeek 识别意图为 `PROFESSIONAL`
2. 调用得理API检索相关法规
3. 将检索结果交给 DeepSeek
4. 生成专业回答

**输出**:
```
根据《劳动合同法》第十九条规定：
- 劳动合同期限三个月以上不满一年的，试用期不得超过一个月；
- 劳动合同期限一年以上不满三年的，试用期不得超过二个月；
- 三年以上固定期限和无固定期限的劳动合同，试用期不得超过六个月。
```

### 场景 2: 通用问题

**用户输入**: "今天天气怎么样？"

**处理流程**:
1. DeepSeek 识别意图为 `WEATHER`
2. 直接由 DeepSeek 回答

**输出**:
```
抱歉，我无法实时获取天气信息。建议您查看天气应用或网站获取准确的天气预报。
```

### 场景 3: 日常闲聊

**用户输入**: "你好，今天心情怎么样？"

**处理流程**:
1. DeepSeek 识别意图为 `CHAT`
2. 直接由 DeepSeek 回答

**输出**:
```
你好！作为AI助手，我随时准备为您提供帮助。有什么我能帮您的吗？
```

## 性能优化

### 1. 缓存策略

```java
@Service
public class IntentRouterService {

    @Cacheable(value = "intentCache", key = "#question")
    public Intent analyzeIntent(String question) {
        // 意图分析逻辑
    }
}
```

### 2. 异步处理

```java
@Async
public CompletableFuture<RouteResponse> routeQuestionAsync(String question) {
    return CompletableFuture.completedFuture(routeQuestion(question));
}
```

### 3. 请求限流

```java
@RateLimiter(value = 100, timeout = 60)
public RouteResponse routeQuestion(String question) {
    // 路由逻辑
}
```

## 监控与日志

### 1. 关键指标

- 意图识别准确率
- 路由决策耗时
- API 调用成功率
- 缓存命中率

### 2. 日志格式

```
2024-03-30 10:00:00 - 意图识别: PROFESSIONAL, 置信度: 0.92, 耗时: 150ms
2024-03-30 10:00:01 - 路由决策: PROFESSIONAL_WITH_SEARCH, 检索结果: 5条, 总耗时: 1200ms
```

## 常见问题

### Q1: 如何提高意图识别准确率？

A: 优化 Prompt 提示词，提供更多示例数据，定期调整置信度阈值。

### Q2: 如何处理网络错误？

A: 实现重试机制（最多3次），使用指数退避策略，提供降级方案。

### Q3: 如何扩展新的意图类型？

A: 在 `Intent` 枚举中添加新类型，在 Prompt 中添加描述，更新路由逻辑。

## 未来规划

1. **意图识别优化**: 引入更多法律领域示例，提高识别准确率
2. **多轮对话支持**: 支持上下文理解，优化连续对话体验
3. **A/B 测试框架**: 对比不同路由策略的效果
4. **实时监控**: 集成 Prometheus + Grafana 监控系统性能

## 技术栈

- **后端**: Spring Boot 3.2 + Java 17
- **AI模型**: DeepSeek API
- **法律检索**: 得理法搜 API
- **前端**: React + TypeScript + Ant Design

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License
