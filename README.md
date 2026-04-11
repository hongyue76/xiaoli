# 律法先锋 - 法律AI智能平台

基于得理法搜(小理AI)大模型构建的法律人工智能助手，提供法律咨询、文书生成、案例检索、合同审查等全方位法律服务。

---

> 🔐 **商标声明**

本项目代码、文档及界面中提及的第三方名称、商标、品牌等，均属于其各自所有者所有：

| 商标/名称 | 所有者 | 用途说明 |
|-----------|--------|----------|
| **得理法搜 / 得理AI / Delilegal** | 得理科技(深圳)有限公司 | API服务提供商 |
| **DeepSeek** | 深度求索(杭州)人工智能科技有限公司 | AI模型提供商 |
| **腾讯 / 腾讯云 / 腾讯元器** | 深圳市腾讯计算机系统有限公司 | 云服务提供商 |
| **Ant Design** | 蚂蚁集团 | UI组件库 |
| **TDesign** | 腾讯 | UI组件库 |
| **Spring Boot** | VMware, Inc. | 后端框架 |
| **React / React.js** | Meta Platforms, Inc. | 前端框架 |
| **Vite** | Evan You | 构建工具 |
| **PostgreSQL** | PostgreSQL Global Development Group | 数据库 |
| **Redis** | Redis Ltd. | 缓存服务 |
| **Docker** | Docker, Inc. | 容器化 |
| **Kubernetes / K8s** | The Linux Foundation | 容器编排 |

**本项目与上述任何公司/组织均无关联关系，亦不代表其观点或立场。使用第三方服务时请遵守其服务条款和隐私政策。**

---

> ⚠️ **重要声明**
>
> 本项目提供的所有法律咨询、文书生成、案例检索、案件分析等服务**仅供参考**，不构成正式的法律意见或建议。
>
> **特别提醒：**
> - AI生成内容可能存在错误或遗漏，请谨慎使用
> - 涉及重要法律事务时，请咨询专业律师
> - 本平台不对使用AI生成内容造成的任何损失承担责任
> - 本平台不提供律师执业服务，不具有法律效力
> - 用户应对使用本平台服务的后果承担全部责任
>
> **适用范围：**
> 本系统适合用于法律知识学习、案例研究、文书草稿准备等辅助性工作，不适用于需要专业法律意见的重要法律事务。

## 功能特性

### 核心功能

> 📋 **注意：以下所有功能均由AI驱动，生成内容仅供参考，不构成法律意见**

- **智能意图路由** - 基于 DeepSeek AI 智能识别用户意图，自动选择最优处理策略
- **法律咨询** - 智能法律问答，提供婚姻家庭、合同纠纷、劳动争议等专业法律咨询（仅供参考）
- **法律文书自动生成** - 起诉状、答辩状、代理词等法律文书智能生成（草稿参考，需专业律师审核）
- **案例智能检索** - 基于向量数据库的语义化案例检索（辅助研究工具）
- **合同条款审查** - 合同风险识别、条款合规性检查（初步筛查建议）
- **案件分析** - 案件要素提取、法律适用分析、诉讼策略建议（分析参考）
- **证据材料分析** - 证据三性分析、证明力评估（辅助分析工具）
- **司法辅助决策** - 量刑建议、审判预测、判决建议（预测参考，不具备法律效力）
- **企业合规管理** - 企业合规审查、风险识别、整改跟踪（合规辅助工具）

### 特色功能
- **语音对话** - 支持语音输入输出的智能法律助手(ASR+TTS)
- **多端覆盖** - Web端、移动端多端支持

## 技术栈

### 后端
- **框架**: Spring Boot 3.x
- **数据库**: PostgreSQL
- **缓存**: Redis
- **AI**: DeepSeek API + 得理法搜(小理AI)大模型
- **意图路由**: 智能识别用户意图，自动选择处理策略
- **语音**: 腾讯云ASR/TTS
- **通信**: WebSocket + REST

### 前端
- **Web**: React 18 + TypeScript + Vite
- **UI**: Ant Design / TDesign

## 项目结构

```
xiaoli/
├── backend/                    # 后端服务
│   ├── pom.xml                 # 父POM
│   ├── common/                 # 公共模块
│   │   ├── common-core/        # 核心工具(统一响应、异常处理)
│   │   ├── common-ai/          # AI公共模块(DeepSeek API)
│   │   ├── ai-core/           # 得理法搜AI服务(对话/Embedding/TTS/ASR)
│   │   ├── vector-core/       # 向量数据库服务(Milvus)
│   │   ├── pdf-core/          # PDF文档服务
│   │   └── ocr-core/          # OCR识别服务
│   ├── intent-core/           # 意图路由核心(8087)
│   ├── ms-consult/            # 法律咨询服务(8081)
│   ├── ms-document/           # 法律文书服务(8082)
│   ├── ms-caseinfo/           # 案例检索服务(8083)
│   ├── ms-contract/           # 合同审查服务(8084)
│   ├── analysis/              # 案件分析服务(8085)
│   ├── ms-decision/           # 司法决策服务(8086)
│   ├── ms-compliance/         # 企业合规服务(8087)
│   ├── ms-evidence/           # 证据分析服务(8088)
│   └── ms-speech/             # 语音对话服务(8089)
│
├── frontend/                   # 前端应用
│   └── web/                    # Web管理后台
│       ├── src/
│       │   ├── pages/          # 页面组件
│       │   │   ├── Consult/   # 法律咨询
│       │   │   ├── Document/  # 文书生成
│       │   │   └── CaseSearch/# 案例检索
│       │   └── services/       # API服务
│       └── package.json
│
├── ai/                         # AI能力模块
│   ├── xiaoli-sdk/            # 得理法搜SDK
│   ├── embedding/             # 向量化服务
│   ├── vector-db/             # 向量数据库配置
│   └── speech/                # 语音处理
│
└── deployment/                 # 部署配置
    └── docker/                # Docker配置
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- PostgreSQL 15+
- Redis 7+

### 本地开发

1. **配置数据库**
```sql
CREATE DATABASE xiaoli_legal;
```

2. **配置得理法搜(小理AI) API**

在各个模块的 `application.yml` 中配置：

```yaml
xiaoli:
  ai:
    base-url: https://openapi.delilegal.com
    api-key: your-api-key

delilegal:
  base-url: https://openapi.delilegal.com
  api-key: your-api-key
```

**获取API Key：**

1. 访问 [得理开放平台](https://open.delilegal.com/) 注册企业账号
2. 联系在线客服申请API访问权限
3. 获取 `API Key`
4. 将参数填入上述配置中
5. **客服电话**: 0755-26907610

**Docker环境配置：**
在环境变量中配置：
```bash
XIAOLI_AI_BASE_URL=https://openapi.delilegal.com
XIAOLI_AI_API_KEY=your-api-key
DELILEGAL_BASE_URL=https://openapi.delilegal.com
DELILEGAL_API_KEY=your-api-key
```

3. **启动后端服务**
```bash
cd backend
mvn clean install

# 启动单个模块
cd ms-consult
mvn spring-boot:run
```

4. **启动前端**
```bash
cd frontend/web
npm install
npm run dev
```

## API配置详情

### 案例检索 API

**接口地址**: `https://openapi.delilegal.com/api/qa/v3/search/queryCase`

**功能描述**: 提供基于关键词或语义的案例检索能力，支持按法院层级、文书类型等条件筛选

**主要参数**:

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| keywordArr | Array[String] | 否 | 关键词数组，如 `["上班途中工伤案例"]` |
| longText | String | 否 | 长文本语义检索（与 keywordArr 二选一） |
| courtLevelArr | Array[String] | 否 | 法院层级筛选 |
| judgementTypeArr | Array[String] | 否 | 文书类型筛选 |

**注意事项**:
- `keywordArr` 参数**必须**是数组类型 `[str]`，即使只有一个关键词也需要用数组形式传入
- `keywordArr` 和 `longText` 参数二选一，不能同时使用
- `courtLevelArr` 示例值: `["最高院", "高院", "中院"]`
- `judgementTypeArr` 示例值: `["判决书", "裁定书"]`

**请求示例**:

```javascript
// 关键词检索
{
  "keywordArr": ["劳动合同", "经济补偿"],
  "courtLevelArr": ["中院"],
  "judgementTypeArr": ["判决书"]
}

// 语义检索
{
  "longText": "关于交通事故中工伤认定的问题分析"
}
```

### 法规检索 API

**接口地址**: `https://openapi.delilegal.com/api/qa/v3/search/queryListLaw`

**功能描述**: 提供法律、法规、司法解释等法律文书的检索能力

**主要参数**:

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| keywords | Array[String] | 是 | 查询关键词或语义文本 |
| fieldName | String | 是 | 检索模式: `"title"`(关键词) 或 `"semantic"`(语义) |

**注意事项**:
- `keywords` 参数同样**必须**是数组类型 `[str]`
- `fieldName` 指定检索方式：
  - `"title"`: 基于标题/关键词的精准检索
  - `"semantic"`: 基于语义理解的智能检索

**请求示例**:

```javascript
// 关键词检索
{
  "keywords": ["劳动合同法"],
  "fieldName": "title"
}

// 语义检索
{
  "keywords": ["员工被无故辞退如何维权"],
  "fieldName": "semantic"
}
```

### 主要环境变量配置

| 变量名 | 说明 | 示例 |
|-------|------|------|
| XIAOLI_AI_BASE_URL | 得理法搜API基础URL | `https://openapi.delilegal.com` |
| XIAOLI_AI_API_KEY | 得理法搜API密钥 | `your-api-key-here` |
| DELILEGAL_BASE_URL | 案例检索API基础URL | `https://openapi.delilegal.com` |
| DELILEGAL_API_KEY | 案例检索API密钥 | `your-api-key-here` |

**获取API Key**:
1. 访问 [得理开放平台](https://open.delilegal.com/) 注册企业账号
2. 联系客服申请API访问权限: 0755-26907610
3. 获取API Key并配置到环境变量中

## 已实现模块

### 公共模块 (core = 公共能力封装)

| 模块 | 说明 |
|------|------|
| common-core | 核心工具：统一响应、异常处理、基类 |
| ai-core | 得理法搜AI服务：对话、Embedding、TTS、ASR |
| vector-core | 向量数据库：Milvus集成 |
| pdf-core | PDF文档：生成、导出、水印 |
| ocr-core | OCR识别：图片/PDF文字提取 |

### 后端微服务

| 模块 | 端口 | 功能 | 状态 |
|------|------|------|------|
| ms-consult | 8081 | 法律咨询、智能问答 | ✅ |
| ms-document | 8082 | 文书生成、模板管理、PDF导出 | ✅ |
| ms-caseinfo | 8083 | 案例检索、语义搜索、向量检索 | ✅ |
| ms-contract | 8084 | 合同审查、风险评估 | ✅ |
| analysis | 8085 | 案件分析、法律适用 | ✅ |
| ms-decision | 8086 | 司法决策、量刑建议 | ✅ |
| ms-compliance | 8087 | 企业合规、风险管控 | ✅ |
| ms-evidence | 8088 | 证据分析、三性审查 | ✅ |
| ms-speech | 8089 | 语音对话、ASR/TTS | ✅ |

### API接口概览

**法律咨询 (ms-consult)**
- `POST /api/consult/chat` - 智能对话
- `GET /api/consult/ask` - 简单问答
- `GET /api/consult/history` - 历史记录

**文书生成 (ms-document)**
- `GET /api/document/templates` - 模板列表
- `POST /api/document/generate` - 生成文书
- `GET /api/document/{id}` - 文书详情

**案例检索 (ms-caseinfo)**
- `POST /api/case/search` - 案例检索
- `GET /api/case/{id}` - 案例详情

**合同审查 (ms-contract)**
- `POST /api/contract/review` - 合同审查
- `GET /api/contract/{id}/review-result` - 审查结果

**案件分析 (analysis)**
- `POST /api/analysis/case` - 案件分析

**司法决策 (ms-decision)**
- `POST /api/decision/generate` - 决策建议
- `POST /api/decision/sentencing` - 量刑建议
- `POST /api/decision/trial-prediction` - 审判预测

**企业合规 (ms-compliance)**
- `POST /api/compliance/company` - 企业管理
- `POST /api/compliance/review` - 合规审查
- `GET /api/compliance/risk/identify` - 风险识别

**证据分析 (ms-evidence)**
- `POST /api/evidence/analyze` - 证据分析

**语音服务 (ms-speech)**
- `POST /api/speech/asr` - 语音识别
- `POST /api/speech/tts` - 语音合成
- `POST /api/speech/chat` - 语音对话

## 部署配置

### Docker部署(推荐)

1. **克隆项目**
```bash
cd d:/me/project/xiaoli
```

2. **配置环境变量**
```bash
cd deployment/docker
cp .env.example .env
# 编辑 .env 填入实际配置
```

3. **启动所有服务**
```bash
docker-compose up -d
```

4. **验证服务**
```bash
# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| PostgreSQL | 5432 | 数据库 |
| Redis | 6379 | 缓存 |
| ms-consult | 8081 | 法律咨询 |
| ms-document | 8082 | 文书生成 |
| ms-caseinfo | 8083 | 案例检索 |
| ms-contract | 8084 | 合同审查 |
| analysis | 8085 | 案件分析 |
| ms-decision | 8086 | 司法决策 |
| ms-compliance | 8087 | 企业合规 |
| ms-evidence | 8088 | 证据分析 |
| ms-speech | 8089 | 语音对话 |
| Frontend | 80 | Web前端 |
| Nginx | 80/443 | 网关 |

### Kubernetes部署

```bash
# 创建命名空间
kubectl create namespace xiaoli-legal

# 部署配置
kubectl apply -f deployment/kubernetes/

# 查看部署状态
kubectl get pods -n xiaoli-legal
```

## 开发指南

### 添加新功能
1. 在对应微服务模块中创建业务逻辑
2. 定义API接口(Controller)
3. 编写Service接口和实现
4. 配置application.yml

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用Spring Boot最佳实践
- 所有API返回统一Result格式

## 项目文件清单

### 部署配置
- `deployment/docker/docker-compose.yml` - Docker编排配置
- `deployment/docker/Dockerfile.backend` - 后端镜像
- `deployment/docker/Dockerfile.frontend` - 前端镜像
- `deployment/docker/.env.example` - 环境变量模板
- `deployment/docker/nginx/nginx.conf` - Nginx配置
- `deployment/docker/init/sql/init.sql` - 数据库初始化
- `deployment/kubernetes/deployment.yaml` - K8s部署
- `deployment/kubernetes/configmap.yaml` - K8s配置
- `deployment/kubernetes/service.yaml` - K8s服务

## 联系方式

- **得理开放平台**: https://open.delilegal.com/
- **得理官方网站**: https://www.delilegal.com/
- **客服电话**: 0755-26907610

## 许可证

本项目采用 MIT License 开源协议。

**许可证说明：**
- ✅ 允许商业使用
- ✅ 允许修改和分发
- ✅ 允许私人使用
- ❌ 需要包含版权声明和许可证
- ❌ 软件按"原样"提供，不提供任何明示或暗示的保证

**详细免责声明请参阅：**
- [DISCLAIMER.md](./DISCLAIMER.md) - 完整的法律和商标免责声明
- [LICENSE](./LICENSE) - MIT 许可证全文

**简要法律免责：**
- 本软件提供的所有AI生成内容仅供参考，不构成法律意见
- 用户对使用本软件产生的结果承担全部责任
- 开发者不对因使用本软件造成的任何损失承担责任
- 重要法律事务请咨询专业律师
