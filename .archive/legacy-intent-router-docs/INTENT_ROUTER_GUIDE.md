# 意图识别和双引擎路由模块

## 功能概述

intent-core模块是律博士系统的核心智能路由层，负责：
1. **意图识别** - 自动识别用户查询的意图类型
2. **双引擎路由** - 智能选择规则引擎或AI大模型引擎
3. **统一响应** - 整合多个AI引擎的输出结果

## 架构设计

### 系统架构

```
用户查询
    ↓
意图识别服务 (IntentRecognitionService)
    ↓
    ├─ 识别意图类型
    ├─ 计算置信度
    └─ 提取关键词
    ↓
引擎路由服务 (EngineRouterService)
    ↓
    ├─ 评估查询复杂度
    ├─ 计算切换阈值
    └─ 选择最佳引擎
    ↓
    ├─→ 规则引擎 (RuleEngineProcessor)
    │       ├─ 模板化响应
    │       ├─ 关键词匹配
    │       └─ 快速检索
    │
    └─→ LLM引擎 (LLMEngineProcessor)
            ├─ DeepSeek API
            ├─ 流式生成
            └─ 上下文理解
    ↓
统一响应 (IntentRouterService)
```

## 核心功能

### 1. 意图识别

**支持意图类型**:
- `CONSULT` - 法律咨询
- `CASE_SEARCH` - 案例检索
- `DOCUMENT` - 文书生成
- `CONTRACT_REVIEW` - 合同审查
- `CASE_ANALYSIS` - 案件分析
- `EVIDENCE_ANALYSIS` - 证据分析
- `DECISION` - 司法决策
- `COMPLIANCE` - 合规检查

**识别方法**:
- 基于关键词匹配
- 正则表达式模式
- 置信度评分
- 备选意图推荐

### 2. 双引擎路由

**规则引擎特点**:
- 响应速度快（~500ms）
- 成本低
- 适合结构化任务
- 支持模板生成

**LLM引擎特点**:
- 理解能力强
- 生成质量高
- 支持复杂推理
- 成本较高（~2s）

**路由策略**:
1. **基于意图类型** - 不同意图有默认引擎
2. **基于复杂度** - 评估查询复杂度选择引擎
3. **基于置信度** - 低置信度使用LLM提高准确率
4. **混合模式** - 规则+LLM组合使用

### 3. 响应生成

**规则引擎响应**:
- 模板化内容
- 关键词匹配结果
- 快速检索案例

**LLM引擎响应**:
- 上下文理解
- 深度推理
- 个性化生成
- 流式输出

## API接口

### 统一查询接口

```http
POST /api/intent-router/query
Content-Type: application/json

{
  "queryText": "我想咨询一个合同纠纷问题",
  "sessionId": "session-123",
  "userId": "user-456",
  "context": {}
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "queryText": "我想咨询一个合同纠纷问题",
    "intentResult": {
      "intent": "CONSULT",
      "confidence": 0.85,
      "keywords": ["合同", "纠纷"],
      "explanation": "根据关键词匹配，识别为法律咨询意图，置信度0.85",
      "alternatives": []
    },
    "engineResult": {
      "engineType": "LLM_BASED",
      "reason": "根据意图类型法律咨询，使用默认AI大模型引擎",
      "expectedResponseTime": 2000,
      "estimatedCost": "中等",
      "hybridMode": false
    },
    "response": "根据您描述的合同纠纷问题...",
    "responseTime": 1850,
    "success": true
  }
}
```

### 仅识别意图

```http
POST /api/intent-router/recognize

Response:
{
  "code": 200,
  "data": {
    "intent": "CASE_SEARCH",
    "confidence": 0.92,
    "keywords": ["案例", "检索"],
    "explanation": "...",
    "alternatives": [...]
  }
}
```

### 仅路由到引擎

```http
POST /api/intent-router/route

Response:
{
  "code": 200,
  "data": {
    "engineType": "RULE_BASED",
    "reason": "...",
    "expectedResponseTime": 500,
    "estimatedCost": "低",
    "hybridMode": false
  }
}
```

### 强制指定意图

```http
POST /api/intent-router/force

Request:
{
  "queryText": "帮我写一份合同",
  "expectedIntent": "DOCUMENT",
  "forceIntent": true
}
```

## 配置说明

### DeepSeek API配置

在 `application.yml` 中配置：

```yaml
deepseek:
  api:
    key: ${DEEPSEEK_API_KEY:your-api-key}
    url: https://api.deepseek.com/v1/chat/completions
```

### 路由阈值配置

可在 `EngineRouterServiceImpl` 中调整：

```java
// 复杂度阈值
INTENT_COMPLEXITY_THRESHOLD.put(IntentType.CONSULT, 0.4);

// 置信度阈值
private static final double CONFIDENCE_THRESHOLD = 0.7;
```

## 使用示例

### Java后端调用

```java
@Autowired
private IntentRouterService intentRouterService;

// 处理查询
UserQuery query = UserQuery.builder()
    .queryText("我遇到了合同纠纷，想咨询一下")
    .sessionId("session-123")
    .userId("user-456")
    .build();

QueryResponse response = intentRouterService.processQuery(query);

// 获取结果
System.out.println("意图: " + response.getIntentResult().getIntent());
System.out.println("引擎: " + response.getEngineResult().getEngineType());
System.out.println("回复: " + response.getResponse());
```

### 前端调用

```typescript
// 统一查询
const response = await fetch('http://localhost:8080/api/intent-router/query', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    queryText: '我想咨询一个问题',
    sessionId: 'session-123',
    userId: 'user-456'
  })
});

const data = await response.json();
console.log(data.data.response);
```

## 性能指标

| 指标 | 规则引擎 | LLM引擎 |
|-----|---------|---------|
| 响应时间 | ~500ms | ~2000ms |
| 成本 | 低 | 中等 |
| 准确率 | 70-80% | 85-95% |
| 适用场景 | 简单查询、模板生成 | 复杂推理、个性化回答 |

## 扩展开发

### 添加新意图

1. 在 `IntentType` 枚举中添加新意图
2. 在 `IntentRecognitionServiceImpl` 中配置关键词
3. 根据需要调整路由策略

### 添加新引擎

1. 实现 `RuleEngineProcessor` 或 `LLMEngineProcessor` 接口
2. 在 `IntentRouterServiceImpl` 中注入新引擎
3. 更新路由逻辑

### 自定义路由策略

继承或修改 `EngineRouterServiceImpl`，实现自定义路由逻辑：

```java
@Override
public EngineSelectionResult route(UserQuery query, IntentResult intentResult) {
    // 自定义路由逻辑
    return EngineSelectionResult.builder()...build();
}
```

## 最佳实践

1. **高优先级查询** - 直接使用LLM引擎
2. **批量查询** - 使用规则引擎提高效率
3. **敏感内容** - 使用规则引擎避免AI幻觉
4. **个性化需求** - 使用LLM引擎提供定制化服务
5. **成本优化** - 根据实际情况调整路由阈值

## 注意事项

1. 确保DeepSeek API密钥正确配置
2. 规则引擎需要定期更新模板和关键词
3. LLM引擎响应时间较长，建议显示加载状态
4. 混合模式需要平衡成本和质量
5. 监控各引擎的准确率和响应时间

## 故障排查

### 问题1: 意图识别不准确
**解决**: 增加关键词样本，调整置信度阈值

### 问题2: LLM引擎响应慢
**解决**: 降低复杂度阈值，优先使用规则引擎

### 问题3: API调用失败
**解决**: 检查API密钥和网络连接，实现降级策略

## 未来规划

- [ ] 机器学习意图识别
- [ ] 多LLM引擎支持（OpenAI、通义千问等）
- [ ] 强化学习路由优化
- [ ] A/B测试引擎选择
- [ ] 实时性能监控
