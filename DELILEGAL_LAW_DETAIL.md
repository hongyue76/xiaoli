# 得理法搜 - 法规详情获取功能

## 功能概述

本次更新新增了**法规详情获取**功能，可以在法规检索后自动获取每个法规的完整正文内容。

---

## 新增接口

### 1. 获取法规详情（单个）

**接口地址**: `GET /api/case/delilegal/law/detail`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| lawId | String | ✅ | 法规ID（从检索结果中获取） |
| merge | boolean | 否 | 是否合并内容（默认true，返回完整正文） |

**请求示例**:
```bash
curl "http://localhost:18083/api/case/delilegal/law/detail?lawId=xxx123&merge=true"
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "lawId": "xxx123",
    "lawTitle": "中华人民共和国劳动合同法",
    "lawDetailContent": "第一章 总则\n第一条 为了完善劳动合同制度，明确劳动合同双方当事人的权利和义务，保护劳动者的合法权益，构建和发展和谐稳定的劳动关系，制定本法。\n第二条 中华人民共和国境内的企业、个体经济组织、民办非企业单位等组织（以下称用人单位）与劳动者建立劳动关系，订立、履行、变更、解除或者终止劳动合同，适用本法。\n...",
    "publishDate": "2012-12-28",
    "effectiveDate": "2013-07-01"
  }
}
```

---

### 2. 批量获取法规详情

**接口地址**: `POST /api/case/delilegal/law/batch`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| lawIds | String | ✅ | 法规ID列表（JSON数组或逗号分隔） |
| merge | boolean | 否 | 是否合并内容（默认true） |

**请求示例**:
```bash
# JSON数组格式
curl -X POST "http://localhost:18083/api/case/delilegal/law/batch?merge=true" \
  -H "Content-Type: application/json" \
  -d '["lawId1", "lawId2", "lawId3"]'

# 逗号分隔格式
curl -X POST "http://localhost:18083/api/case/delilegal/law/batch?lawIds=lawId1,lawId2,lawId3&merge=true"
```

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "lawId": "lawId1",
      "fullContentPreview": "第一章 总则\n为了完善劳动合同制度...",
      "fullContent": "第一章 总则\n为了完善劳动合同制度，明确劳动合同双方当事人的权利和义务...",
      "success": true,
      "detail": { ... }
    },
    {
      "lawId": "lawId2",
      "fullContent": null,
      "fullContentPreview": null,
      "success": false,
      "error": "404 Not Found"
    }
  ]
}
```

---

## 增强功能

### 法规检索自动获取详情

法规检索接口 `/api/case/delilegal/law` 新增参数 `autoFetchDetail`：

**请求示例**:
```bash
curl -X POST "http://localhost:18083/api/case/delilegal/law" \
  -H "Content-Type: application/json" \
  -d '{
    "keywords": ["劳动合同"],
    "fieldName": "title",
    "autoFetchDetail": true
  }'
```

**响应增强**:
```json
{
  "code": 200,
  "data": [
    {
      "id": "lawId1",
      "title": "中华人民共和国劳动合同法",
      "summary": "...",
      "fullContent": "第一章 总则\n为了完善劳动合同制度...",
      "fullContentPreview": "第一章 总则\n为了完善劳动合同制度...",
      "detailSuccess": true
    },
    {
      "id": "lawId2",
      "title": "劳动合同法实施条例",
      "summary": "...",
      "fullContent": null,
      "fullContentPreview": null,
      "detailSuccess": false,
      "detailError": "网络请求超时"
    }
  ]
}
```

---

## 性能优化

### 并发请求

- 使用 `CompletableFuture` 实现异步并发
- 线程池配置：最大10个并发线程
- 批量请求超时：30秒
- 单个请求失败不影响其他请求

### 响应字段说明

| 字段 | 说明 |
|------|------|
| fullContent | 完整法规正文（从 `lawDetailContent` 提取） |
| fullContentPreview | 前500字符预览 |
| detailSuccess | 是否成功获取详情 |
| detailError | 失败时的错误信息 |

---

## 错误处理

### 容错机制

1. **单个法规失败不影响整体**：失败的法规会标记 `fullContent: null`，但其他法规正常返回
2. **超时保护**：单个请求超时（默认OkHttp超时），记录错误并继续
3. **日志记录**：详细记录请求成功/失败数量

### 常见错误

| 错误 | 原因 | 处理方式 |
|------|------|----------|
| 404 | 法规ID不存在 | 标记 `fullContent: null` |
| 500 | 服务器内部错误 | 标记 `fullContent: null` |
| timeout | 请求超时 | 标记 `fullContent: null` |
| network | 网络异常 | 标记 `fullContent: null` |

---

## 代码变更

### 修改的文件

1. **DelilegalService.java** - 新增接口方法
   ```java
   String getLawDetail(String lawId, boolean merge);
   String getBatchLawDetails(List<String> lawIds, boolean merge);
   ```

2. **DelilegalServiceImpl.java** - 实现详情获取逻辑
   - 新增 `get()` 方法支持 GET 请求
   - 新增 `getLawDetail()` 单个详情获取
   - 新增 `getBatchLawDetails()` 批量并发获取
   - 使用 `CompletableFuture` 实现异步并发

3. **CaseController.java** - 新增 API 端点
   - `GET /delilegal/law/detail` - 单个详情
   - `POST /delilegal/law/batch` - 批量详情
   - 修改 `/delilegal/law` 支持自动获取详情

---

## 使用建议

### 1. 实时获取详情
```typescript
// 自动获取（推荐）
const result = await caseAPI.searchLaws({
  keywords: ['劳动合同'],
  fieldName: 'title',
  autoFetchDetail: true
});
```

### 2. 手动控制获取
```typescript
// 先搜索
const searchResult = await caseAPI.searchLaws({
  keywords: ['劳动合同'],
  fieldName: 'title'
});

// 选择性获取详情
const lawIds = searchResult.data.map(law => law.id);
const details = await caseAPI.getBatchLawDetails(lawIds, true);
```

### 3. 前端展示优化
```typescript
// 根据 detailSuccess 显示不同内容
{law.detailSuccess ? (
  <div className="law-content">
    {law.fullContentPreview}
    <Button onClick={() => showFullContent(law.fullContent)}>
      查看完整内容
    </Button>
  </div>
) : (
  <Alert 
    type="warning" 
    message={`详情获取失败: ${law.detailError}`}
    action={<Button size="small" onClick={() => retryFetch(law.id)}>重试</Button>}
  />
)}
```

---

## 配置说明

无需额外配置，直接使用现有得理法搜 API 配置：

```yaml
delilegal:
  base-url: ${DELILEGAL_BASE_URL:https://openapi.delilegal.com}
  app-id: ${DELILEGAL_APP_ID:your-app-id}
  secret: ${DELILEGAL_SECRET:your-secret}
  timeout: 30000
```
