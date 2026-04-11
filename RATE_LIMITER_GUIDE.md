# 接口防刷限流使用指南

## 概述

基于 Redis + Lua 实现的高性能接口限流机制，支持滑动窗口算法，可有效防止接口刷屏、恶意攻击等。

## 核心特性

- ✅ **高性能**: 使用 Lua 脚本保证原子性
- ✅ **滑动窗口**: 更精准的限流控制
- ✅ **灵活配置**: 支持自定义限流键、窗口大小、最大请求数
- ✅ **多种标识**: 支持 IP、用户 ID 等多种限流标识
- ✅ **友好异常**: 统一的异常处理和错误提示
- ✅ **易于使用**: 仅需一个注解即可实现限流

## 架构设计

### 组件结构

```
common-core/
└── ratelimiter/
    ├── RateLimiter.java                    # 限流注解
    ├── RateLimitException.java             # 限流异常
    ├── RedisRateLimiterService.java        # Redis 限流服务
    ├── RateLimiterAspect.java              # 限流切面
    ├── RateLimitExceptionHandler.java       # 异常处理器
    └── RateLimiterExampleController.java   # 示例控制器
```

### 工作流程

```
1. 用户请求
   ↓
2. AOP 拦截
   ↓
3. 提取限流配置
   ↓
4. 生成限流键（基于 IP/用户ID）
   ↓
5. 执行 Lua 脚本检查限流
   ↓
6. 未超出限流 → 允许请求
   ↓
7. 超出限流 → 抛出异常
   ↓
8. 异常处理器返回友好错误
```

## 快速开始

### 基本使用

在 Controller 方法上添加 `@RateLimiter` 注解：

```java
@RestController
@RequestMapping("/api/consult")
public class ConsultController {

    @RateLimiter(
            value = "api:consult",
            maxRequests = 100,
            duration = 60
    )
    @PostMapping("/ask")
    public Result ask(@RequestBody ConsultRequest request) {
        // 业务逻辑
        return Result.success(answer);
    }
}
```

### 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| value | String | "api:default" | 限流键前缀 |
| maxRequests | int | 100 | 最大请求数 |
| duration | int | 60 | 时间窗口（秒） |
| identifier | String | "" | 限流标识符（默认使用 IP） |
| message | String | "请求过于频繁" | 限流提示信息 |

## 使用场景

### 场景 1: 基于 IP 的限流

每个 IP 在 60 秒内最多访问 10 次：

```java
@RateLimiter(
        value = "api:example:ip",
        maxRequests = 10,
        duration = 60
)
@GetMapping("/data")
public Result getData() {
    return Result.success(data);
}
```

### 场景 2: 基于用户 ID 的限流

每个用户在 10 秒内最多访问 5 次：

```java
@RateLimiter(
        value = "api:example:user",
        maxRequests = 5,
        duration = 10,
        identifier = "user:" + userId  // 从 token 中获取用户ID
)
@PostMapping("/action")
public Result doAction(@CurrentUser Long userId) {
    return Result.success(result);
}
```

### 场景 3: 严格限流（防刷）

每个 IP 在 1 秒内最多访问 1 次：

```java
@RateLimiter(
        value = "api:submit",
        maxRequests = 1,
        duration = 1,
        message = "操作太快了，请慢一点"
)
@PostMapping("/submit")
public Result submit(@RequestBody Data data) {
    return Result.success(result);
}
```

### 场景 4: 日限额流

每个 IP 在 24 小时内最多访问 1000 次：

```java
@RateLimiter(
        value = "api:daily",
        maxRequests = 1000,
        duration = 86400,  // 24 小时
        message = "您今天的访问次数已达上限"
)
@GetMapping("/api-endpoint")
public Result getEndpointData() {
    return Result.success(data);
}
```

### 场景 5: 法律咨询限流

每个用户在 1 分钟内最多咨询 3 次：

```java
@RateLimiter(
        value = "api:consult",
        maxRequests = 3,
        duration = 60,
        identifier = "user:" + userId,
        message = "咨询过于频繁，请 1 分钟后再试"
)
@PostMapping("/consult")
public Result consult(@CurrentUser Long userId, @RequestBody ConsultRequest request) {
    return Result.success(answer);
}
```

### 场景 6: 文书生成限流

每个用户在 10 分钟内最多生成 5 份文书：

```java
@RateLimiter(
        value = "api:document:generate",
        maxRequests = 5,
        duration = 600,
        identifier = "user:" + userId,
        message = "文书生成过于频繁，请 10 分钟后再试"
)
@PostMapping("/document/generate")
public Result generateDocument(@CurrentUser Long userId, @RequestBody GenerateRequest request) {
    return Result.success(document);
}
```

## 限流算法

### 滑动窗口算法

使用 Redis 的 Sorted Set 实现：

```lua
-- 1. 删除过期的请求记录
redis.call('ZREMRANGEBYSCORE', key, '-inf', windowStart)

-- 2. 获取当前窗口内的请求数
local current = redis.call('ZCARD', key)

-- 3. 检查是否超出限流
if current >= maxRequests then
    return {1, current}
end

-- 4. 添加当前请求
redis.call('ZADD', key, now, now)
redis.call('EXPIRE', key, duration + 1)

return {0, current + 1}
```

### 优势

- ✅ 精确控制：基于时间窗口的精确控制
- ✅ 平滑限流：避免固定窗口的边界问题
- ✅ 内存高效：自动清理过期数据
- ✅ 分布式支持：天然支持分布式环境

## 配置示例

### 不同接口的限流配置

| 接口类型 | 限流策略 | maxRequests | duration | identifier |
|---------|---------|-------------|----------|------------|
| 登录接口 | 严格 | 5 | 60 | IP |
| 咨询接口 | 中等 | 3 | 60 | 用户ID |
| 文书生成 | 宽松 | 5 | 600 | 用户ID |
| 数据查询 | 宽松 | 100 | 60 | 用户ID |
| 提交接口 | 严格 | 1 | 1 | 用户ID |

### application.yml 配置

无需额外配置，使用现有 Redis 配置即可：

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:Xiaoli@2024}
```

## 错误处理

### 限流异常响应

当请求超出限流时，返回以下错误：

```json
{
  "code": 429,
  "message": "请求过于频繁，请稍后再试",
  "data": null
}
```

### 自定义错误信息

```java
@RateLimiter(
        value = "api:consult",
        maxRequests = 3,
        duration = 60,
        message = "咨询过于频繁，请 1 分钟后再试"
)
```

### 监控日志

限流触发时会记录日志：

```
WARN  Rate limit exceeded: key=api:consult:user:123:1711881600, maxRequests=3, duration=60, currentRequests=3
```

## 测试方法

### 使用 curl 测试

```bash
# 测试严格限流（1 秒内最多 1 次）
for i in {1..5}; do
  curl -X POST http://localhost:8081/api/example/test
  echo
done
```

### 使用 JMeter 测试

1. 创建线程组
2. 添加 HTTP 请求
3. 设置循环次数 > 限流阈值
4. 运行测试，观察响应

### 使用 Postman 测试

1. 创建 Collection
2. 使用 Collection Runner
3. 设置迭代次数 > 限流阈值
4. 查看测试结果

## 监控指标

### 关键指标

1. **限流触发次数**
   ```bash
   # 查看限流键数量
   redis-cli KEYS "api:*" | wc -l
   ```

2. **当前请求数**
   ```bash
   # 查看特定键的请求数
   redis-cli ZCARD "api:consult:127.0.0.1:1711881600"
   ```

3. **限流命中率**
   ```bash
   # 通过日志统计限流触发次数
   grep "Rate limit exceeded" application.log | wc -l
   ```

### Prometheus 指标

建议添加以下指标：

```java
@Component
public class RateLimitMetrics {

    private final Counter rateLimitCounter;

    public RateLimitMetrics(MeterRegistry registry) {
        this.rateLimitCounter = Counter.builder("rate.limit.exceeded")
                .description("Rate limit exceeded count")
                .tag("endpoint", "unknown")
                .register(registry);
    }

    public void recordRateLimit(String endpoint) {
        rateLimitCounter.increment();
    }
}
```

## 最佳实践

### 1. 合理设置限流阈值

根据接口特点设置合理的限流阈值：

| 接口 | 建议阈值 | 说明 |
|------|---------|------|
| 登录 | 5 次/分钟 | 防止暴力破解 |
| 咨询 | 3 次/分钟 | 平衡用户体验 |
| 查询 | 100 次/分钟 | 允许正常使用 |
| 提交 | 1 次/秒 | 防止重复提交 |

### 2. 使用合适的标识符

- **IP 限流**: 适用于未登录用户
- **用户 ID 限流**: 适用于已登录用户
- **组合限流**: IP + 用户 ID 双重限流

### 3. 分层限流

实现多级限流策略：

```java
// 第一层：IP 限流
@RateLimiter(value = "ip:api", maxRequests = 100, duration = 60)

// 第二层：用户限流
@RateLimiter(value = "user:api", maxRequests = 10, duration = 60, identifier = "user:" + userId)

// 第三层：全局限流
@RateLimiter(value = "global:api", maxRequests = 1000, duration = 60, identifier = "global")
```

### 4. 降级策略

Redis 异常时允许请求通过（已实现）：

```java
try {
    // 尝试限流
    boolean allowed = rateLimiterService.tryAcquire(key, maxRequests, duration);
    return allowed;
} catch (Exception e) {
    // Redis 异常时降级，允许请求通过
    return true;
}
```

### 5. 监控和告警

- 监控限流触发频率
- 设置告警阈值
- 定期分析限流日志

## 常见问题

### Q: 如何查看当前的限流状态？

A: 使用 Redis 命令查看：

```bash
# 查看所有限流键
redis-cli KEYS "api:*"

# 查看特定键的请求数
redis-cli ZCARD "api:consult:127.0.0.1:1711881600"

# 查看窗口内的所有请求时间戳
redis-cli ZRANGE "api:consult:127.0.0.1:1711881600" 0 -1
```

### Q: 如何临时禁用某个接口的限流？

A: 注释掉 `@RateLimiter` 注解即可。

### Q: 如何实现动态调整限流阈值？

A: 将限流配置存储在数据库或配置中心，动态加载：

```java
@RateLimiter(
        value = "api:consult",
        maxRequests = "${consult.max-requests:3}",
        duration = "${consult.duration:60}"
)
```

### Q: 如何处理限流后的用户体验？

A: 返回友好的错误信息，并提示等待时间：

```java
@RateLimiter(
        value = "api:consult",
        maxRequests = 3,
        duration = 60,
        message = "咨询过于频繁，请 1 分钟后再试"
)
```

### Q: 限流会占用大量 Redis 内存吗？

A: 不会。Redis 会自动清理过期数据，每个限流键只保留窗口内的请求记录。

## 性能优化

### 1. Lua 脚本优化

- 使用原子操作
- 减少网络往返
- 批量执行命令

### 2. 键设计优化

- 使用合理的键前缀
- 避免键冲突
- 定期清理无用键

### 3. 连接池优化

确保 Redis 连接池配置合理：

```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 16
          max-idle: 8
          min-idle: 2
```

## 相关文档

- [Redis 官方文档](https://redis.io/docs/)
- [Spring AOP 文档](https://docs.spring.io/spring-framework/reference/core/aop.html)

## 更新日志

| 日期 | 版本 | 更新内容 |
|------|------|----------|
| 2026-03-31 | 1.0.0 | 初始版本，实现接口限流机制 |

## 总结

基于 Redis + Lua 的接口限流机制已完全集成到 `common-core` 模块：

**核心优势**:
- ✅ 高性能（Lua 原子操作）
- ✅ 精确控制（滑动窗口算法）
- ✅ 易于使用（注解驱动）
- ✅ 分布式支持（Redis 集群）

**使用步骤**:
1. 在接口方法上添加 `@RateLimiter` 注解
2. 配置限流参数（maxRequests, duration）
3. 选择限流标识符（IP/用户ID）
4. 自定义错误信息（可选）

**适用场景**:
- 防止接口刷屏
- 防止恶意攻击
- 保护后端服务
- 平衡用户体验
