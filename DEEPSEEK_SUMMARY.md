# DeepSeek 意图路由系统 - 功能总结

## 🎯 项目概述

本项目成功实现了基于 DeepSeek API 的智能意图路由系统，能够自动识别用户意图，并智能选择最优的处理策略。

## ✨ 核心功能

### 1. 智能意图识别

- **6 种意图类型**: PROFESSIONAL、GENERAL、CHAT、WEATHER、SUMMARY、AMBIGUOUS
- **置信度评估**: 0-1 范围的置信度评分
- **判断原因**: 每个意图识别都附带详细原因说明

### 2. 智能路由决策

| 意图类型 | 处理策略 | API 调用 |
|---------|---------|---------|
| PROFESSIONAL | 专业模式 | 得理API + DeepSeek |
| GENERAL | 通用模式 | DeepSeek |
| CHAT | 闲聊模式 | DeepSeek |
| WEATHER | 天气模式 | 天气API |
| SUMMARY | 总结模式 | DeepSeek |
| AMBIGUOUS | 澄清模式 | 询问用户 |

### 3. 完整的 API 接口

#### 意图分析 API
- **接口**: `POST /intent-router/analyze`
- **功能**: 分析用户问题的意图类型
- **响应**: 意图类型、置信度、判断原因

#### 智能问答 API
- **接口**: `POST /intent-router/chat`
- **功能**: 根据意图自动路由，提供智能回答
- **响应**: 回答内容、处理类型、检索结果、耗时

#### 健康检查 API
- **接口**: `GET /intent-router/health`
- **功能**: 检查服务运行状态

### 4. 前端组件

- **IntentAwareAIChat**: 完整的智能聊天组件
- **IntentRouterDemo**: 意图路由演示页面
- **intentRouterService**: 前端服务封装

## 📁 项目结构

```
xiaoli/
├── backend/
│   ├── common/common-ai/              # AI 公共模块
│   │   ├── config/
│   │   │   └── DeepSeekConfig.java   # DeepSeek 配置
│   │   ├── model/
│   │   │   ├── Intent.java           # 意图模型
│   │   │   ├── DeepSeekRequest.java  # 请求模型
│   │   │   └── DeepSeekResponse.java # 响应模型
│   │   └── service/
│   │       ├── DeepSeekService.java  # DeepSeek 服务
│   │       └── IntentRouterService.java # 意图路由服务
│   └── intent-core/                   # 意图路由核心
│       ├── controller/
│       │   └── IntentRouterController.java # 控制器
│       ├── IntentRouterApplication.java   # 启动类
│       └── pom.xml                   # Maven 配置
├── frontend/web/
│   ├── src/
│   │   ├── components/
│   │   │   └── AIChat/
│   │   │       ├── IntentAwareAIChat.tsx # 智能聊天组件
│   │   │       └── IntentAwareAIChat.css
│   │   ├── pages/
│   │   │   └── IntentRouterDemo/      # 演示页面
│   │   ├── services/
│   │   │   ├── api.ts                # API 封装
│   │   │   └── intentRouterService.ts # 意图路由服务
│   │   └── App.tsx                   # 路由配置
├── DEEPSEEK_INTENT_ROUTER.md         # 系统架构文档
├── DEEPSEEK_INTEGRATION_GUIDE.md      # 集成指南
├── DEEPSEEK_DEPLOYMENT.md            # 部署指南
├── start-intent-router.bat           # Windows 启动脚本
├── start-intent-router.sh            # Linux/Mac 启动脚本
└── test-intent-router.ps1            # 测试脚本
```

## 🚀 快速开始

### Windows 用户

```powershell
# 1. 配置 API Key
# 编辑 backend/intent-core/src/main/resources/application.properties
# 设置 deepseek.api.key=your-api-key

# 2. 启动服务
.\start-intent-router.bat

# 3. 运行测试
.\test-intent-router.ps1
```

### Linux/Mac 用户

```bash
# 1. 配置 API Key
# 编辑 backend/intent-core/src/main/resources/application.properties
# 设置 deepseek.api.key=your-api-key

# 2. 启动服务
chmod +x start-intent-router.sh
./start-intent-router.sh

# 3. 访问演示页面
# 浏览器打开 http://localhost:5173/intent-router-demo
```

## 📊 测试用例

### 专业法律问题测试

```bash
# 测试 1: 劳动合同法
问题: "劳动合同法规定试用期多长？"
期望: PROFESSIONAL + PROFESSIONAL_WITH_SEARCH

# 测试 2: 合同纠纷
问题: "合同违约需要承担什么法律责任？"
期望: PROFESSIONAL + PROFESSIONAL_WITH_SEARCH
```

### 通用问题测试

```bash
# 测试 3: 天气查询
问题: "今天天气怎么样？"
期望: WEATHER + GENERAL_WITHOUT_SEARCH

# 测试 4: 闲聊
问题: "你好，今天心情怎么样？"
期望: CHAT + GENERAL_WITHOUT_SEARCH
```

### 内容总结测试

```bash
# 测试 5: 内容总结
问题: "请帮我总结一下这段话的主要内容"
期望: SUMMARY + GENERAL_WITHOUT_SEARCH
```

## 🔧 配置说明

### DeepSeek API 配置

```properties
# API 密钥（必填）
deepseek.api.key=sk-your-api-key

# API 地址（可选，默认：https://api.deepseek.com/v1）
deepseek.api.base-url=https://api.deepseek.com/v1

# 模型名称（可选，默认：deepseek-chat）
deepseek.api.model=deepseek-chat

# 超时时间（可选，默认：30000ms）
deepseek.api.timeout=30000

# 最大重试次数（可选，默认：3）
deepseek.api.max-retries=3
```

### 意图识别配置

```properties
# 置信度阈值（可选，默认：0.6）
intent.confidence-threshold=0.6

# 启用降级（可选，默认：true）
intent.enable-fallback=true

# 降级意图（可选，默认：GENERAL）
intent.fallback-intent=GENERAL
```

## 📈 性能指标

### 预期性能

| 指标 | 目标值 |
|-----|--------|
| 意图识别准确率 | > 90% |
| 平均响应时间 | < 2s |
| API 调用成功率 | > 95% |
| 并发处理能力 | > 100 QPS |

### 优化建议

1. **启用缓存**: 对相同问题的意图分析结果进行缓存
2. **异步处理**: 使用 `@Async` 异步调用 DeepSeek API
3. **连接池**: 配置 OkHttp 连接池提高并发性能
4. **限流保护**: 实现请求限流，防止 API 调用过载

## 🔒 安全建议

1. **API Key 保护**
   - 不要将 API Key 提交到代码仓库
   - 使用环境变量或密钥管理服务
   - 定期轮换 API Key

2. **访问控制**
   - 启用 CORS 限制
   - 实现用户认证
   - 限制请求频率

3. **数据加密**
   - 使用 HTTPS 传输
   - 加密敏感数据

## 📚 相关文档

- [系统架构文档](./DEEPSEEK_INTENT_ROUTER.md)
- [集成指南](./DEEPSEEK_INTEGRATION_GUIDE.md)
- [部署指南](./DEEPSEEK_DEPLOYMENT.md)

## 🎓 使用示例

### 后端调用

```java
@RestController
@RequestMapping("/custom")
public class CustomController {

    @Autowired
    private IntentRouterService intentRouterService;

    @PostMapping("/analyze")
    public ResponseEntity<Intent> analyze(@RequestBody String question) {
        Intent intent = intentRouterService.analyzeIntent(question);
        return ResponseEntity.ok(intent);
    }
}
```

### 前端调用

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

## 🐛 故障排查

### 常见问题

1. **DeepSeek API 调用失败**
   - 检查 API Key 是否正确
   - 检查网络连接
   - 查看 API 配额

2. **意图识别不准确**
   - 调整置信度阈值
   - 优化 Prompt 提示词
   - 添加更多示例数据

3. **服务启动失败**
   - 检查端口是否被占用
   - 检查 Java 版本
   - 查看日志文件

## 🎉 功能亮点

✅ **智能意图识别**: 基于 DeepSeek AI，准确识别 6 种意图类型
✅ **自动路由决策**: 根据意图自动选择最优处理策略
✅ **专业法律支持**: 集成得理法搜 API，提供专业法律检索
✅ **完整前端组件**: React 组件开箱即用
✅ **详细文档**: 完整的架构、集成、部署文档
✅ **测试脚本**: 自动化功能测试
✅ **快速启动**: 一键启动脚本

## 📝 更新日志

### v1.0.0 (2024-03-30)

**新增功能**:
- ✅ DeepSeek API 集成
- ✅ 智能意图识别（6 种类型）
- ✅ 自动路由决策
- ✅ 意图分析 API
- ✅ 智能问答 API
- ✅ 健康检查 API
- ✅ React 智能聊天组件
- ✅ 前端演示页面
- ✅ 完整文档
- ✅ 启动脚本
- ✅ 测试脚本

**技术特性**:
- Spring Boot 3.x + Java 17
- React 18 + TypeScript
- OkHttp 连接池
- 异步处理支持
- 缓存支持

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

---

**快速链接**:
- [架构文档](./DEEPSEEK_INTENT_ROUTER.md)
- [集成指南](./DEEPSEEK_INTEGRATION_GUIDE.md)
- [部署指南](./DEEPSEEK_DEPLOYMENT.md)
- [README](./README.md)
