# DeepSeek 意图路由系统部署指南

## 前置条件

### 1. 获取 DeepSeek API Key

访问 [DeepSeek 官网](https://platform.deepseek.com/) 注册账号并获取 API Key。

### 2. 环境要求

- **Java**: JDK 17+
- **Maven**: 3.8+
- **Node.js**: 18+
- **数据库**: PostgreSQL 14+ (可选)
- **缓存**: Redis 7+ (可选)

## 快速部署

### 步骤 1: 配置后端

编辑 `backend/intent-core/src/main/resources/application.properties`：

```properties
# DeepSeek API 配置
deepseek.api.key=sk-your-actual-api-key-here
deepseek.api.base-url=https://api.deepseek.com/v1
deepseek.api.model=deepseek-chat
deepseek.api.timeout=30000
deepseek.api.max-retries=3

# 意图识别配置
intent.confidence-threshold=0.6
intent.enable-fallback=true
intent.fallback-intent=GENERAL

# 服务配置
server.port=8087
server.servlet.context-path=/intent-router
```

### 步骤 2: 编译后端

```bash
cd backend
mvn clean compile -DskipTests
```

### 步骤 3: 启动后端服务

```bash
cd intent-core
mvn spring-boot:run
```

服务将在 `http://localhost:8087/intent-router` 启动。

### 步骤 4: 配置前端

在 `frontend/web/.env` 中添加：

```env
VITE_API_BASE_URL=http://localhost:8087
```

### 步骤 5: 启动前端

```bash
cd frontend/web
npm install
npm run dev
```

前端将在 `http://localhost:5173` 启动。

## 验证部署

### 1. 健康检查

```bash
curl http://localhost:8087/intent-router/health
```

预期响应：
```json
{
  "status": "UP",
  "timestamp": 1711752000000
}
```

### 2. 意图分析测试

```bash
curl -X POST http://localhost:8087/intent-router/analyze \
  -H "Content-Type: application/json" \
  -d '{"question":"劳动合同法规定试用期多长？"}'
```

### 3. 智能问答测试

```bash
curl -X POST http://localhost:8087/intent-router/chat \
  -H "Content-Type: application/json" \
  -d '{"question":"劳动合同法规定试用期多长？","userId":1}'
```

### 4. 访问演示页面

打开浏览器访问：`http://localhost:5173/intent-router-demo`

## Docker 部署

### 后端 Dockerfile

创建 `backend/intent-core/Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/intent-core-1.0.0.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 构建和运行

```bash
# 构建
cd backend/intent-core
mvn clean package -DskipTests
docker build -t intent-router:1.0.0 .

# 运行
docker run -d \
  -p 8087:8087 \
  -e DEEPSEEK_API_KEY=your-api-key \
  intent-router:1.0.0
```

## 生产环境配置

### 1. 使用环境变量

```bash
export DEEPSEEK_API_KEY=sk-production-key
export DELILEGAL_API_BASE_URL=https://api.delilegal.com
export REDIS_HOST=your-redis-host
export DB_HOST=your-db-host
```

### 2. 配置反向代理 (Nginx)

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location /intent-router/ {
        proxy_pass http://localhost:8087/intent-router/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location / {
        proxy_pass http://localhost:5173;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 3. 启用 HTTPS

使用 Let's Encrypt 获取免费 SSL 证书：

```bash
sudo certbot --nginx -d your-domain.com
```

## 监控和日志

### 1. 日志配置

在 `application.properties` 中：

```properties
# 日志级别
logging.level.com.xiaoli.intent=INFO
logging.level.com.xiaoli.ai.common=INFO

# 日志文件
logging.file.name=/var/log/intent-router/app.log
logging.file.max-size=100MB
logging.file.max-history=30
```

### 2. 监控指标

关键监控指标：
- 意图识别准确率
- API 调用成功率
- 平均响应时间
- 错误率

### 3. 告警配置

设置告警规则：
- API 调用失败率 > 5%
- 平均响应时间 > 3s
- 意图识别置信度 < 0.5

## 故障排查

### 问题 1: DeepSeek API 调用失败

**症状**: 返回 "DeepSeek API 调用失败" 错误

**解决方案**:
1. 检查 API Key 是否正确
2. 检查网络连接
3. 查看 API 配额是否用完
4. 检查 API URL 是否正确

### 问题 2: 意图识别不准确

**症状**: 专业问题被识别为通用问题

**解决方案**:
1. 调整 `intent.confidence-threshold`
2. 优化 Prompt 提示词
3. 添加更多示例数据
4. 检查 DeepSeek 模型版本

### 问题 3: 服务启动失败

**症状**: 服务无法启动

**解决方案**:
1. 检查端口 8087 是否被占用
2. 检查 Java 版本是否为 17+
3. 查看日志文件获取详细错误
4. 检查依赖是否正确安装

## 性能优化

### 1. 启用缓存

```java
@Cacheable(value = "intentCache", key = "#question")
public Intent analyzeIntent(String question) {
    // 意图分析逻辑
}
```

### 2. 配置连接池

```java
@Configuration
public class OkHttpConfig {

    @Bean
    public OkHttpClient deepSeekClient() {
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }
}
```

### 3. 异步处理

```java
@Async
public CompletableFuture<RouteResponse> routeQuestionAsync(String question) {
    return CompletableFuture.completedFuture(routeQuestion(question));
}
```

## 安全建议

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
   - 定期备份数据

## 维护

### 定期任务

- 每日检查日志
- 每周审查性能指标
- 每月更新依赖包
- 每季度审查安全策略

### 更新升级

```bash
# 更新后端
cd backend
git pull
mvn clean package
# 重启服务

# 更新前端
cd frontend/web
git pull
npm install
npm run build
# 重启前端服务
```

## 支持

如遇到问题，请：
1. 查看日志文件
2. 检查配置文件
3. 查看 [FAQ](./DEEPSEEK_INTEGRATION_GUIDE.md#常见问题)
4. 提交 Issue

## 附录

### A. 环境变量参考

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DEEPSEEK_API_KEY | DeepSeek API 密钥 | - |
| DEEPSEEK_API_URL | DeepSeek API 地址 | https://api.deepseek.com/v1 |
| SERVER_PORT | 服务端口 | 8087 |
| REDIS_HOST | Redis 主机 | localhost |
| REDIS_PORT | Redis 端口 | 6379 |

### B. 端口说明

| 端口 | 服务 | 说明 |
|------|------|------|
| 8087 | 意图路由服务 | 后端 API |
| 5173 | 前端开发服务器 | 开发环境 |
| 80 | HTTP 反向代理 | 生产环境 |
| 443 | HTTPS 反向代理 | 生产环境 |

### C. 相关文档

- [系统架构文档](./DEEPSEEK_INTENT_ROUTER.md)
- [集成指南](./DEEPSEEK_INTEGRATION_GUIDE.md)
- [API 文档](./DEEPSEEK_INTENT_ROUTER.md#api-详解)
