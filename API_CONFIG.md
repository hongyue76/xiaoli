# 得理法搜(小理AI) API配置指南

## 得理法搜(小理AI) API配置

### 1. 获取API密钥

1. 访问 [得理开放平台](https://open.delilegal.com/)
2. 注册企业账号并完成认证
3. 联系在线客服申请API访问权限
4. 获取以下信息:
   - `API Key`: API访问密钥
   - **客服电话**: 0755-26907610

### 2. 配置方式

#### 方式一: 修改 `.env` 文件

编辑项目根目录下的 `.env` 文件:

```bash
# 得理法搜(小理AI) API配置
XIAOLI_AI_BASE_URL=https://openapi.delilegal.com
XIAOLI_AI_API_KEY=your-api-key

# 得理法搜案例检索配置
DELILEGAL_BASE_URL=https://openapi.delilegal.com
DELILEGAL_API_KEY=your-api-key
```

#### 方式二: 直接修改 `application.yml`

编辑各个微服务的 `application.yml` 文件:

```yaml
xiaoli:
  ai:
    base-url: https://openapi.delilegal.com
    api-key: your-api-key
    model: xiaoli-pro

delilegal:
  base-url: https://openapi.delilegal.com
  api-key: your-api-key
```

#### 方式三: 通过环境变量设置

在启动服务时设置环境变量:

**Windows (PowerShell):**
```powershell
$env:XIAOLI_AI_BASE_URL="https://openapi.delilegal.com"
$env:XIAOLI_AI_API_KEY="your-api-key"
$env:DELILEGAL_API_KEY="your-api-key"
cd backend/ms-consult
mvn spring-boot:run
```

**Linux/Mac:**
```bash
export XIAOLI_AI_BASE_URL="https://openapi.delilegal.com"
export XIAOLI_AI_API_KEY="your-api-key"
export DELILEGAL_API_KEY="your-api-key"
cd backend/ms-consult
mvn spring-boot:run
```

### 3. 需要配置的微服务

以下服务需要配置得理法搜(小理AI) API:

1. **ms-consult** (法律咨询服务) - 端口 8081
2. **ms-case** (案例检索服务) - 端口 8083
3. **ms-speech** (语音对话服务) - 端口 8089

### 4. 验证配置

配置完成后,启动服务并测试:

```bash
# 测试法律咨询API
curl -X POST http://localhost:8081/api/consult/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"你好,请帮我分析一下劳动合同纠纷"}'
```

### 5. 常见问题

**Q: API调用失败怎么办?**

A: 检查以下几点:
1. assistant-id和token是否正确
2. 网络连接是否正常
3. API额度是否充足
4. 查看服务日志中的详细错误信息

**Q: 如何测试API是否可用?**

A: 使用curl或Postman测试:
```bash
curl https://openapi.delilegal.com/api/qa/v3/search/queryListCase \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"keywords":"劳动合同纠纷"}'
```

**Q: API有调用限制吗?**

A: 得理法搜API有调用频率和配额限制,具体限制需要联系客服获取详细信息。

### 6. 相关链接

- [得理法搜开放平台](https://open.delilegal.com/)
- [得理官方网站](https://www.delilegal.com/)
- **客服电话**: 0755-26907610

