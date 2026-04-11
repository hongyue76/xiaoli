# 法律AI助手系统架构设计

## 一、系统架构总览

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              客户端层                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│   Web前端 (React + TypeScript)                                              │
└────────────────────────────────┬────────────────────────────────────────────┘
                                 │ RESTful API / WebSocket
                                 ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              业务服务层 (Spring Boot)                         │
├──────────┬──────────┬──────────┬──────────┬──────────┬──────────┬──────────┤
│ ms-consult│ ms-document│ ms-caseinfo│ ms-contract│ analysis  │ ms-decision│
│ (8081)   │ (8082)   │ (8083)   │ (8084)   │ (8085)   │ (8086)   │
├──────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ ms-compliance│ ms-evidence│ ms-speech│                                    │
│ (8087)   │ (8088)   │ (8089)   │                                    │
└──────────┴──────────┴──────────┴────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              AI能力层                                        │
├────────────────┬────────────────┬────────────────┬───────────────────────────┤
│  小理AI       │  语音服务      │  向量数据库    │  OCR识别                 │
│  Open API     │  ASR/TTS       │  (设计)        │  (设计)                  │
└───────────────┴────────────────┴────────────────┴───────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              数据存储层                                       │
├────────────────┬────────────────┬───────────────────────────────────────────┤
│  PostgreSQL   │  Redis         │  文件存储(设计)                           │
│  (业务数据)    │  (缓存/会话)   │                                          │
└───────────────┴────────────────┴───────────────────────────────────────────┘
```

## 二、技术栈选型

### 后端技术栈
| 层次 | 技术选型 | 说明 |
|------|----------|------|
| 框架 | Spring Boot 3.x | 微服务架构 |
| 数据库 | PostgreSQL | 结构化业务数据 |
| 缓存 | Redis | 会话、热点数据 |
| AI | 小理AI Open API | 法律问答与生成 |
| 语音 | 腾讯云ASR/TTS | 语音识别与合成 |
| WebSocket | Spring WebSocket | 实时语音对话 |

### 前端技术栈
| 类型 | 技术选型 | 说明 |
|------|----------|------|
| Web前端 | React 18 + TypeScript | 企业级管理后台 |
| 构建工具 | Vite | 快速开发 |
| UI框架 | Ant Design | 组件库 |

## 三、核心功能模块

### 3.1 法律咨询模块 (ms-consult)
**端口**: 8081

**功能**:
- 智能法律问答
- 多轮对话管理
- 咨询历史记录
- 咨询分类导航

**核心类**:
- `XiaoliChatService` - 小理AI对接
- `ConsultService` - 咨询业务逻辑
- `ConsultController` - API接口

### 3.2 法律文书模块 (ms-document)
**端口**: 8082

**功能**:
- 文书模板管理
- AI辅助文书生成
- 文书预览与下载

**核心类**:
- `DocumentService` - 文书服务
- `DocumentTemplate` - 模板实体
- `LegalDocument` - 文书实体

### 3.3 案例检索模块 (ms-caseinfo)
**端口**: 8083

**功能**:
- 案例关键词检索
- 语义相似度检索(设计)
- 相似案例推荐
- 热门案例排行

**核心类**:
- `CaseSearchService` - 检索服务
- `LegalCase` - 案例实体

### 3.4 合同审查模块 (ms-contract)
**端口**: 8084

**功能**:
- 合同完整性审查
- 合法性审查
- 公平性审查
- 风险评估

**核心类**:
- `ContractService` - 审查服务
- `Contract` - 合同实体
- `ContractIssue` - 问题条款

### 3.5 案件分析模块 (analysis)
**端口**: 8085

**功能**:
- 事实梳理
- 法律适用分析
- 争议焦点提取
- 诉讼策略建议

**核心类**:
- `CaseAnalysisService` - 分析服务
- `CaseAnalysis` - 分析结果实体

### 3.6 证据分析模块 (ms-evidence)
**端口**: 8088

**功能**:
- 证据三性分析(真实性、合法性、关联性)
- 证明力评估
- 质证意见生成

**核心类**:
- `EvidenceService` - 证据服务
- `Evidence` - 证据实体
- `EvidenceAnalysis` - 分析结果

### 3.7 司法决策模块 (ms-decision)
**端口**: 8086

**功能**:
- 量刑建议
- 审判预测
- 相似判例检索
- 风险评估

**核心类**:
- `DecisionService` - 决策服务
- `CaseDecision` - 决策结果实体

### 3.8 企业合规模块 (ms-compliance)
**端口**: 8087

**功能**:
- 企业信息管理
- 多类型合规审查
- 风险识别与整改
- 合规报告生成

**核心类**:
- `ComplianceService` - 合规服务
- `Company` - 企业实体
- `ComplianceRisk` - 风险实体

### 3.9 语音对话模块 (ms-speech)
**端口**: 8089

**功能**:
- 语音识别(ASR)
- 语音合成(TTS)
- 实时语音对话(WebSocket)
- 会话管理

**核心类**:
- `AsrService` - 语音识别
- `TtsService` - 语音合成
- `VoiceChatService` - 语音对话
- `WebSocketConfig` - WebSocket配置

## 四、项目结构

```
xiaoli/
├── backend/                         # 后端服务
│   ├── pom.xml                      # 父POM
│   ├── common/                      # 公共模块
│   │   └── common-core/             # 核心工具
│   │       └── com/xiaoli/legal/common/core/
│   │           ├── domain/          # 统一响应
│   │           │   ├── Result.java
│   │           │   ├── ResultCode.java
│   │           │   └── PageResult.java
│   │           ├── exception/       # 异常处理
│   │           └── constant/        # 常量
│   │
│   ├── ms-consult/                  # 法律咨询(8081)
│   │   ├── model/
│   │   │   ├── dto/                 # 请求/响应DTO
│   │   │   ├── entity/              # 实体类
│   │   │   └── vo/                  # 视图对象
│   │   ├── service/
│   │   ├── controller/
│   │   └── mapper/
│   │
│   ├── ms-document/                 # 文书生成(8082)
│   ├── ms-caseinfo/                # 案例检索(8083)
│   ├── ms-contract/                 # 合同审查(8084)
│   ├── analysis/                    # 案件分析(8085)
│   ├── ms-decision/                # 司法决策(8086)
│   ├── ms-compliance/              # 企业合规(8087)
│   ├── ms-evidence/                 # 证据分析(8088)
│   └── ms-speech/                  # 语音对话(8089)
│
├── frontend/                        # 前端应用
│   └── web/                        # Web管理后台
│       ├── src/
│       │   ├── pages/
│       │   │   ├── Consult/         # 法律咨询页
│       │   │   ├── Document/        # 文书生成页
│       │   │   └── CaseSearch/      # 案例检索页
│       │   ├── services/
│       │   │   └── api.ts           # API封装
│       │   ├── App.tsx
│       │   └── main.tsx
│       ├── package.json
│       ├── vite.config.ts
│       └── tsconfig.json
│
├── ai/                             # AI能力模块(待开发)
│   ├── xiaoli-sdk/
│   ├── embedding/
│   └── vector-db/
│
└── deployment/                     # 部署配置
    └── docker/
```

## 五、数据模型

### 核心实体关系

```
ConsultConversation (咨询会话)
├── id
├── userId
├── title
└── messages: [ConsultMessage]

LegalDocument (法律文书)
├── id
├── title
├── content
├── templateId
├── caseId
└── status

LegalCase (法律案例)
├── id
├── caseNumber
├── caseType
├── title
├── content
└── judgment

Contract (合同)
├── id
├── title
├── content
├── partyA
├── partyB
├── amount
└── reviewResult

CaseAnalysis (案件分析)
├── id
├── caseId
├── facts
├── analysis
├── strategy
└── legalBasis

ComplianceReview (合规审查)
├── id
├── companyId
├── reviewType
├── result
└── riskLevel

SpeechSession (语音会话)
├── id
├── sessionId
├── userId
└── messages: [SpeechMessage]
```

## 六、API统一响应格式

```java
// 成功响应
{
  "code": 200,
  "message": "success",
  "data": {...}
}

// 分页响应
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}

// 失败响应
{
  "code": 500,
  "message": "error message",
  "data": null
}
```

## 七、技术实现路径(已完成)

### 阶段一：基础架构 ✅
- [x] Spring Boot项目初始化
- [x] 公共模块(统一响应、异常处理)
- [x] 微服务模块划分

### 阶段二：核心功能 ✅
- [x] 法律咨询模块
- [x] 文书生成模块
- [x] 案例检索模块
- [x] 合同审查模块

### 阶段三：高级功能 ✅
- [x] 案件分析模块
- [x] 证据分析模块
- [x] 司法决策模块
- [x] 企业合规模块

### 阶段四：语音功能 ✅
- [x] 语音识别(ASR)
- [x] 语音合成(TTS)
- [x] 语音对话
- [x] WebSocket支持

## 八、意图识别和双引擎路由架构

### 8.1 架构设计

```
用户查询 → 意图识别 → 引擎路由 → 响应生成
    ↓            ↓           ↓           ↓
  输入文本    意图类型   规则/LLM    最终答案
               +置信度    智能选择
```

### 8.2 核心组件

#### 意图识别层 (Intent Recognition)
- **服务类**: `IntentRecognitionService`
- **识别类型**:
  - 法律咨询
  - 案例检索
  - 文书生成
  - 合同审查
  - 案件分析
  - 证据分析
  - 司法决策
  - 合规检查

#### 引擎路由层 (Engine Router)
- **服务类**: `EngineRouterService`
- **引擎类型**:
  - 规则引擎 (Rule-Based) - 快速响应，成本低
  - AI大模型 (LLM-Based) - 智能生成，准确率高
- **路由策略**:
  - 基于意图类型
  - 基于查询复杂度
  - 基于置信度
  - 混合模式支持

#### 响应生成层 (Response Generation)
- **规则引擎**: `RuleEngineProcessor`
  - 模板化响应
  - 关键词匹配
  - 快速检索
- **LLM引擎**: `LLMEngineProcessor`
  - DeepSeek API集成
  - 流式生成
  - 上下文理解

### 8.3 路由决策矩阵

| 意图类型 | 默认引擎 | 切换到LLM条件 |
|---------|---------|--------------|
| 案例检索 | 规则引擎 | 复杂度>0.6 或 置信度<0.7 |
| 文书生成 | 规则引擎 | 复杂度>0.5 或 置信度<0.7 |
| 法律咨询 | LLM引擎 | 复杂度<0.4 |
| 合同审查 | LLM引擎 | 复杂度<0.3 |
| 案件分析 | LLM引擎 | 复杂度<0.4 |
| 其他 | LLM引擎 | - |

## 九、待实现功能

- [x] 向量数据库集成(Milvus) - 已实现 vector-core 模块
- [x] 意图识别和双引擎路由 - 已实现 intent-core 模块
- [ ] 用户认证模块 - 规划中
- [ ] API网关 - 规划中
- [x] 完整的前端界面 - 已实现 Web 端
- [x] OCR识别集成 - 已实现 ocr-core 模块
- [x] 部署配置 - 已实现 Docker/Kubernetes 部署
