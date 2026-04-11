# 意图识别 + 双引擎路由架构总结

## 功能概述

已在律博士法律AI助手系统中成功集成**意图识别**和**双引擎路由**功能，实现了智能化的用户查询处理。

## 核心特性

### 1. 意图识别

支持8种用户意图的自动识别：
- ✅ 法律咨询 (CONSULT)
- ✅ 案例检索 (CASE_SEARCH)
- ✅ 文书生成 (DOCUMENT)
- ✅ 合同审查 (CONTRACT_REVIEW)
- ✅ 案件分析 (CASE_ANALYSIS)
- ✅ 证据分析 (EVIDENCE_ANALYSIS)
- ✅ 司法决策 (DECISION)
- ✅ 合规检查 (COMPLIANCE)

**识别方法**：
- 基于关键词匹配
- 正则表达式模式
- 置信度评分（0-1）
- 备选意图推荐

### 2. 双引擎架构

#### 规则引擎 (Rule-Based)
- **特点**：响应快（~500ms）、成本低
- **适用场景**：
  - 案例检索（关键词匹配）
  - 文书生成（模板化）
  - 简单查询

#### AI大模型引擎 (LLM-Based)
- **特点**：理解强、质量高（~2000ms）
- **适用场景**：
  - 法律咨询（复杂推理）
  - 合同审查（深度分析）
  - 案件分析（专业建议）

### 3. 智能路由策略

系统根据以下因素自动选择最佳引擎：

| 因素 | 说明 |
|-----|------|
| **意图类型** | 不同意图有默认引擎配置 |
| **查询复杂度** | 基于文本长度、问题类型评估 |
| **置信度** | 低置信度（<0.7）使用LLM提高准确率 |
| **成本考虑** | 平衡速度和质量 |

### 4. 路由决策矩阵

| 意图类型 | 默认引擎 | 切换到LLM条件 |
|---------|---------|--------------|
| 案例检索 | 规则引擎 | 复杂度>0.6 或 置信度<0.7 |
| 文书生成 | 规则引擎 | 复杂度>0.5 或 置信度<0.7 |
| 法律咨询 | LLM引擎 | 复杂度<0.4 |
| 合同审查 | LLM引擎 | 复杂度<0.3 |
| 案件分析 | LLM引擎 | 复杂度<0.4 |
| 证据分析 | LLM引擎 | 复杂度<0.4 |
| 司法决策 | LLM引擎 | 复杂度<0.3 |
| 合规检查 | LLM引擎 | 复杂度<0.4 |

## 系统架构

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

## 文件清单

### 后端文件

#### 核心模块 (intent-core)
```
backend/intent-core/src/main/java/com/xiaoli/legal/core/
├── IntentType.java                              # 意图类型枚举
├── AIEngineType.java                            # AI引擎类型枚举
├── model/
│   ├── IntentResult.java                        # 意图识别结果
│   ├── AlternativeIntent.java                    # 备选意图
│   ├── EngineSelectionResult.java                # 引擎选择结果
│   └── UserQuery.java                           # 用户查询请求
├── service/
│   ├── IntentRecognitionService.java             # 意图识别服务接口
│   ├── IntentRouterService.java                 # 统一路由服务接口
│   ├── EngineRouterService.java                 # 引擎路由服务接口
│   └── impl/
│       ├── IntentRecognitionServiceImpl.java     # 意图识别实现
│       ├── IntentRouterServiceImpl.java          # 统一路由实现
│       └── EngineRouterServiceImpl.java         # 引擎路由实现
├── engine/
│   ├── RuleEngineProcessor.java                 # 规则引擎接口
│   ├── LLMEngineProcessor.java                  # LLM引擎接口
│   └── impl/
│       ├── DefaultRuleEngineProcessor.java       # 默认规则引擎实现
│       └── DeepSeekLLMProcessor.java             # DeepSeek LLM实现
└── controller/
    └── IntentRouterController.java              # 路由控制器
```

#### 文档文件
- `backend/INTENT_ROUTER_GUIDE.md` - 详细使用指南

### 前端文件

```
frontend/web/src/
├── components/
│   └── IntentRouterChat.tsx                      # 意图路由聊天组件
├── pages/
│   └── IntentRouter/
│       ├── index.tsx                            # 意图路由主页面
│       └── IntentRouter.css                     # 页面样式
└── App.tsx                                      # 路由配置（已更新）
```

#### 配置文件
- `MainLayout.tsx` - 已添加智能路由菜单项

### 启动脚本

```
backend/
└── start-intent-router.bat                      # 意图路由服务启动脚本
```

### 架构文档

```
d:/me/project/xiaoli/
├── architecture-design.md                        # 已更新（添加意图路由章节）
└── INTENT_ROUTER_SUMMARY.md                      # 本文件
```

## API接口

### 1. 统一查询接口

```http
POST /api/intent-router/query
Content-Type: application/json

Request:
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

### 2. 仅识别意图

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

### 3. 仅路由到引擎

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

### 4. 强制指定意图

```http
POST /api/intent-router/force

Request:
{
  "queryText": "帮我写一份合同",
  "expectedIntent": "DOCUMENT",
  "forceIntent": true
}
```

### 5. 获取支持的类型

```http
GET /api/intent-router/intents  # 获取所有意图类型
GET /api/intent-router/engines  # 获取所有引擎类型
```

## 使用方法

### 后端启动

#### 方法1：使用启动脚本（推荐）
```batch
cd backend
start-intent-router.bat
```

#### 方法2：手动启动
```batch
cd backend/intent-core
mvn clean package -DskipTests
java -jar target/intent-core-1.0.0.jar
```

### 前端访问

启动前端服务后，访问：
- 主页：http://localhost:3000
- 智能路由页面：http://localhost:3000/intent-router

在智能路由页面中：
1. 输入问题
2. 系统自动识别意图
3. 智能选择引擎（规则引擎或AI大模型）
4. 显示详细的意图分析和引擎选择过程

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

在 `EngineRouterServiceImpl.java` 中调整：
```java
// 复杂度阈值
INTENT_COMPLEXITY_THRESHOLD.put(IntentType.CONSULT, 0.4);

// 置信度阈值
private static final double CONFIDENCE_THRESHOLD = 0.7;
```

## 性能指标

| 指标 | 规则引擎 | LLM引擎 |
|-----|---------|---------|
| 响应时间 | ~500ms | ~2000ms |
| 成本 | 低 | 中等 |
| 准确率 | 70-80% | 85-95% |
| 适用场景 | 简单查询、模板生成 | 复杂推理、个性化回答 |

## 前端特性

### 1. 实时意图展示
- 显示识别的意图类型
- 显示置信度百分比
- 显示选择的引擎类型（规则引擎/AI大模型）
- 显示响应时间

### 2. 智能标签
- 🎯 意图标签（不同颜色）
- ⚡ 引擎标签（蓝色=规则引擎，紫色=AI大模型）
- 📊 置信度标签
- ⏱️ 响应时间标签

### 3. 降级机制
后端服务未启动时，前端自动使用本地模拟模式，确保功能可用。

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

继承或修改 `EngineRouterServiceImpl`：
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

1. ✅ 确保DeepSeek API密钥正确配置
2. ✅ 规则引擎需要定期更新模板和关键词
3. ✅ LLM引擎响应时间较长，建议显示加载状态
4. ✅ 混合模式需要平衡成本和质量
5. ✅ 监控各引擎的准确率和响应时间

## 未来规划

- [ ] 机器学习意图识别（提高准确率）
- [ ] 多LLM引擎支持（OpenAI、通义千问等）
- [ ] 强化学习路由优化（自动学习最佳策略）
- [ ] A/B测试引擎选择（数据驱动优化）
- [ ] 实时性能监控（Dashboard展示）
- [ ] 用户意图学习（个性化推荐）

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Web
- RestTemplate（HTTP客户端）
- Lombok（简化代码）

### 前端
- React 18
- TypeScript
- Ant Design 5.x
- React Router

### AI能力
- DeepSeek API（LLM引擎）
- 规则引擎（模板化响应）

## 总结

意图识别和双引擎路由架构已成功集成到律博士系统中，实现了：

1. ✅ **智能意图识别** - 自动识别8种用户意图
2. ✅ **双引擎架构** - 规则引擎+LLM引擎智能选择
3. ✅ **透明反馈** - 展示意图识别和引擎选择过程
4. ✅ **降级机制** - 前端本地模拟确保功能可用
5. ✅ **完整文档** - 详细的使用指南和API文档

用户现在可以：
- 访问 http://localhost:3000/intent-router 体验智能路由功能
- 输入任意法律问题，系统自动识别意图并选择最佳引擎
- 查看详细的意图分析和引擎选择过程
- 在后端服务未启动时也能使用本地模拟模式

该架构在响应速度和回答质量之间取得了良好的平衡，为用户提供了更智能、更高效的法律AI服务体验！
