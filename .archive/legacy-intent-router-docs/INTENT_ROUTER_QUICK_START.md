# 意图识别 + 双引擎路由 - 快速启动指南

## 🚀 快速体验（无需后端）

前端已内置降级机制，无需启动后端即可体验！

### 1. 启动前端

```powershell
cd frontend/web
npm run dev
```

### 2. 访问页面

打开浏览器访问：http://localhost:3000/intent-router

### 3. 测试功能

在页面中输入以下示例问题，观察意图识别和引擎选择：

#### 测试案例1 - 案例检索（规则引擎）
```
帮我搜索一下合同纠纷的案例
```
**预期结果**：
- 意图：CASE_SEARCH（案例检索）
- 引擎：规则引擎
- 响应时间：~150ms

#### 测试案例2 - 法律咨询（AI大模型）
```
我遇到了合同纠纷，想咨询一下怎么处理
```
**预期结果**：
- 意图：CONSULT（法律咨询）
- 引擎：AI大模型（模拟）
- 响应时间：~150ms（本地模拟）

#### 测试案例3 - 文书生成（规则引擎）
```
帮我写一份合同书
```
**预期结果**：
- 意图：DOCUMENT（文书生成）
- 引擎：规则引擎
- 响应时间：~150ms

#### 测试案例4 - 合同审查（AI大模型）
```
帮我审查一下这份合同的合法性
```
**预期结果**：
- 意图：CONTRACT_REVIEW（合同审查）
- 引擎：AI大模型（模拟）
- 响应时间：~150ms（本地模拟）

## 📊 观察要点

### 1. 意图识别
- 系统自动识别8种意图类型
- 显示置信度百分比
- 提供备选意图

### 2. 引擎选择
- 🔵 规则引擎（快速响应）
- 🟣 AI大模型（智能生成）
- 显示路由原因

### 3. 元数据展示
- 意图类型
- 置信度
- 引擎类型
- 响应时间

## 🔧 完整体验（需要后端）

如果需要体验真实的双引擎功能，请按以下步骤启动后端：

### 前置条件

1. Java JDK 17或更高版本
2. Maven 3.6或更高版本
3. DeepSeek API密钥（可选，用于LLM引擎）

### 启动后端

```powershell
cd backend
start-intent-router.bat
```

### 配置DeepSeek API

编辑 `backend/intent-core/src/main/resources/application.yml`：

```yaml
deepseek:
  api:
    key: 你的DeepSeek API密钥
    url: https://api.deepseek.com/v1/chat/completions
```

### 测试真实功能

启动后端后，前端会自动连接后端API，提供真实的双引擎功能：
- ✅ 规则引擎：~500ms响应
- ✅ LLM引擎：~2000ms响应
- ✅ 智能路由：自动选择最佳引擎
- ✅ 完整的意图分析和引擎选择过程

## 📚 功能说明

### 支持的意图类型

| 意图 | 说明 | 默认引擎 |
|-----|------|---------|
| CONSULT | 法律咨询 | LLM引擎 |
| CASE_SEARCH | 案例检索 | 规则引擎 |
| DOCUMENT | 文书生成 | 规则引擎 |
| CONTRACT_REVIEW | 合同审查 | LLM引擎 |
| CASE_ANALYSIS | 案件分析 | LLM引擎 |
| EVIDENCE_ANALYSIS | 证据分析 | LLM引擎 |
| DECISION | 司法决策 | LLM引擎 |
| COMPLIANCE | 合规检查 | LLM引擎 |

### 双引擎对比

| 特性 | 规则引擎 | LLM引擎 |
|-----|---------|---------|
| 响应时间 | ~500ms | ~2000ms |
| 成本 | 低 | 中等 |
| 准确率 | 70-80% | 85-95% |
| 适用场景 | 简单查询、模板生成 | 复杂推理、个性化回答 |

## 🎯 测试建议

### 1. 测试意图识别准确性

使用不同关键词组合，观察意图识别是否准确：

```
✓ 正确："案例检索" → CASE_SEARCH
✓ 正确："合同纠纷" → CONSULT
✓ 正确："写一份合同" → DOCUMENT
✓ 正确："审查合同" → CONTRACT_REVIEW
```

### 2. 测试引擎路由策略

观察不同复杂度的查询如何路由到不同引擎：

```
简单查询 → 规则引擎（如："搜索案例"）
复杂查询 → LLM引擎（如："我遇到了合同纠纷，想咨询一下怎么处理"）
```

### 3. 测试降级机制

- 启动前端，不启动后端
- 观察前端是否自动使用本地模拟模式
- 检查是否有降级提示

## 🐛 故障排查

### 问题1：前端无法启动

**解决方案**：
```powershell
# 检查端口占用
netstat -ano | findstr :3000

# 或使用其他端口
npm run dev -- --port 3001
```

### 问题2：后端编译失败

**解决方案**：
```powershell
# 清理并重新编译
cd backend/intent-core
mvn clean
mvn install
```

### 问题3：API调用失败

**解决方案**：
```powershell
# 检查后端服务是否启动
curl http://localhost:8080/api/intent-router/intents

# 查看后端日志
# 检查DeepSeek API密钥是否配置
```

## 📖 更多信息

- 详细架构设计：`architecture-design.md`
- 使用指南：`backend/INTENT_ROUTER_GUIDE.md`
- 架构总结：`INTENT_ROUTER_SUMMARY.md`

## 🎉 开始使用

现在就打开浏览器访问 http://localhost:3000/intent-router ，体验智能意图识别和双引擎路由功能吧！

输入你的第一个法律问题，看看系统如何自动识别意图并选择最佳引擎！
