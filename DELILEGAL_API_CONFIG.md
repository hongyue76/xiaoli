# 得理AI API配置指南

## 概述

本项目集成了得理法搜（得理AI）的API，提供案例检索和法规检索功能。

## API接口

### 案例检索API

**接口地址**: `POST https://openapi.delilegal.com/api/qa/v3/search/queryListCase`

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

### 法规检索API

**接口地址**: `POST https://openapi.delilegal.com/api/qa/v3/search/queryListLaw`

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

## 鉴权方式

**Header鉴权**:

得理AI使用Header传递鉴权信息：

| Header名称 | 说明 | 示例 |
|-----------|------|------|
| appid | 应用ID | `your-app-id` |
| secret | 应用密钥 | `your-secret` |
| Content-Type | 内容类型 | `application/json` |

**示例**:

```http
POST https://openapi.delilegal.com/api/qa/v3/search/queryListCase
Content-Type: application/json
appid: your-app-id
secret: your-secret

{
  "keywordArr": ["劳动合同"]
}
```

## 项目配置

### 后端配置

在 `backend/ms-case/src/main/resources/application.yml` 中配置：

```yaml
# 得理法搜案例检索API
delilegal:
  base-url: ${DELILEGAL_BASE_URL:https://openapi.delilegal.com}
  app-id: ${DELILEGAL_APP_ID:your-app-id}
  secret: ${DELILEGAL_SECRET:your-secret}
  api-key: ${DELILEGAL_API_KEY:your-api-key}
  timeout: 30000
```

### 环境变量配置

在 `.env` 文件中配置：

```bash
# 得理AI配置
DELILEGAL_BASE_URL=https://openapi.delilegal.com
DELILEGAL_APP_ID=your-app-id
DELILEGAL_SECRET=your-secret
DELILEGAL_API_KEY=your-api-key
```

### Docker环境配置

在 `deployment/docker/.env` 中配置：

```bash
# 得理AI配置
XIAOLI_AI_BASE_URL=https://openapi.delilegal.com
XIAOLI_AI_API_KEY=your-api-key
DELILEGAL_BASE_URL=https://openapi.delilegal.com
DELILEGAL_APP_ID=your-app-id
DELILEGAL_SECRET=your-secret
DELILEGAL_API_KEY=your-api-key
```

## 获取API密钥

### 步骤

1. **访问平台**: 打开 [得理开放平台](https://open.delilegal.com/)

2. **注册账号**: 注册企业账号并完成认证

3. **申请权限**: 联系在线客服申请API访问权限

4. **获取密钥**: 获取 `appid` 和 `secret` 参数

5. **配置项目**: 将参数填入配置文件

### 联系方式

- **得理开放平台**: https://open.delilegal.com/
- **得理官方网站**: https://www.delilegal.com/
- **客服电话**: 0755-26907610

## 使用示例

### 前端调用

```typescript
import { caseAPI } from '@/services/api';

// 案例检索（使用得理API）
const searchCases = async () => {
  const data = await caseAPI.searchByDelilegal({
    keywords: ['劳动合同', '经济补偿'],
    courtLevelArr: ['中院'],
    judgementTypeArr: ['判决书']
  });
  console.log(data);
};

// 法规检索
const searchLaws = async () => {
  const data = await caseAPI.searchLaws({
    keywords: ['劳动合同法'],
    fieldName: 'title'
  });
  console.log(data);
};
```

### 后端调用

```java
@RestController
@RequestMapping("/api/case")
public class CaseController {

    @Autowired
    private DelilegalService delilegalService;

    @PostMapping("/delilegal/search")
    public Result<String> searchCases(@RequestBody Map<String, Object> request) {
        List<String> keywords = (List<String>) request.get("keywords");
        String longText = (String) request.get("longText");

        String result = delilegalService.searchCases(keywords, longText, null, null);
        return Result.success(result);
    }
}
```

## 前端界面

在案例检索页面，可以通过以下方式使用得理API：

1. **切换数据源**: 点击搜索框右侧的开关，选择"本地"或"得理API"
2. **选择检索类型**: 在"案例检索"和"法规检索"标签页之间切换
3. **选择检索方式**: 支持"语义检索"和"关键词检索"两种模式

### 功能说明

- **本地数据源**: 使用项目本地数据库进行检索
- **得理API**: 调用得理法搜API进行实时检索
- **语义检索**: 基于AI语义理解进行智能匹配
- **关键词检索**: 基于关键词进行精准匹配

## 错误处理

### 常见错误

1. **401 Unauthorized**: appid或secret配置错误
   - 检查配置文件中的appid和secret是否正确
   - 确认API密钥是否已激活

2. **400 Bad Request**: 请求参数格式错误
   - 确保keywords参数是数组格式
   - 检查Content-Type是否为application/json

3. **500 Internal Server Error**: 服务器内部错误
   - 检查得理AI服务是否正常运行
   - 查看后端日志获取详细错误信息

### 调试建议

1. **查看日志**: 检查后端日志中的API响应
2. **使用测试工具**: 使用Postman或curl测试API
3. **检查网络**: 确保可以访问openapi.delilegal.com
4. **联系客服**: 遇到问题可联系得理官方客服

## 注意事项

1. **API限流**: 注意API调用频率限制，避免触发限流
2. **数据缓存**: 考虑对检索结果进行缓存，减少API调用
3. **错误重试**: 实现错误重试机制，提高系统稳定性
4. **日志记录**: 记录API调用日志，便于问题排查
5. **费用控制**: 关注API调用费用，避免超支

## 技术支持

- **项目文档**: [README.md](../README.md)
- **API文档**: [得理开放平台API文档](https://open.delilegal.com/docs)
- **技术支持**: 得理官方客服 0755-26907610
