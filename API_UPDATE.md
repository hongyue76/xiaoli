# API配置更新说明

## 更新内容

已将项目的AI服务从腾讯元器API切换到得理法搜(小理AI) API。

## 更新的文件

### 1. 配置文件
- ✅ `.env.example` - 环境变量模板已更新
- ✅ `.env` - 实际配置文件已更新
- ✅ `deployment/docker/.env.example` - Docker环境变量模板已更新

### 2. 文档文件
- ✅ `API_CONFIG.md` - API配置指南已更新
- ✅ `README.md` - 项目README已更新

## API配置说明

### 得理法搜(小理AI) API配置

```bash
# 小理AI法律大模型配置
XIAOLI_AI_BASE_URL=https://openapi.delilegal.com
XIAOLI_AI_API_KEY=your-api-key

# 得理法搜案例检索配置
DELILEGAL_BASE_URL=https://openapi.delilegal.com
DELILEGAL_API_KEY=your-api-key
```

## 获取API Key的方式

1. 访问 [得理开放平台](https://open.delilegal.com/)
2. 注册企业账号并完成认证
3. 联系在线客服申请API访问权限
4. 获取API Key
5. **客服电话**: 0755-26907610

## 下一步操作

1. 联系得理开放平台获取API Key
2. 将API Key填入 `.env` 文件中的 `your-api-key` 位置
3. 启动服务测试

## 相关链接

- [得理开放平台](https://open.delilegal.com/)
- [得理官方网站](https://www.delilegal.com/)
- 客服电话: 0755-26907610
