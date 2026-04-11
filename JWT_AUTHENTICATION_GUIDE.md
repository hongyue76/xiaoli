# JWT 统一认证机制使用指南

## 概述

本文档详细说明了小理法律平台统一 JWT 认证机制的实现和使用方法。

## 架构设计

### 模块结构

```
common-core/
└── security/
    ├── JwtTokenProvider.java          # Token 生成和验证
    ├── JwtAuthenticationFilter.java   # JWT 认证过滤器
    ├── SecurityConfig.java            # Spring Security 配置
    ├── UserPrincipal.java             # 用户认证信息
    ├── CurrentUser.java               # 当前用户注解
    ├── CurrentUserArgumentResolver.java  # 参数解析器
    ├── SecurityMvcConfig.java        # MVC 配置
    └── AuthController.java            # 认证示例控制器
```

### 工作流程

```
1. 用户登录
   ↓
2. 服务器验证用户名密码
   ↓
3. 生成 JWT Token (Access Token + Refresh Token)
   ↓
4. 返回 Token 给客户端
   ↓
5. 客户端存储 Token
   ↓
6. 后续请求携带 Token (Authorization: Bearer xxx)
   ↓
7. JwtAuthenticationFilter 拦截并验证 Token
   ↓
8. 设置认证信息到 Security Context
   ↓
9. Controller 方法获取当前用户信息
   ↓
10. 返回业务数据
```

## 配置说明

### application.yml 配置

```yaml
# JWT 配置
jwt:
  # JWT 密钥（生产环境必须修改）
  secret: xiaoli-legal-secret-key-2024-for-jwt-token-generation
  # 访问令牌过期时间（毫秒）- 默认 24 小时
  expiration: 86400000
  # 刷新令牌过期时间（毫秒）- 默认 7 天
  refresh-expiration: 604800000
```

### 环境变量配置

```bash
# 生产环境建议使用环境变量
JWT_SECRET=your-production-secret-key-min-256-bits
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

## 使用方法

### 1. 用户登录

#### 请求示例

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "username": "admin",
    "roles": ["USER", "ADMIN"]
  }
}
```

### 2. 访问受保护接口

#### 请求示例

```bash
GET /api/user/profile
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "email": "admin@example.com"
  }
}
```

### 3. 刷新 Token

#### 请求示例

```bash
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

## Controller 使用示例

### 使用 @CurrentUser 获取当前用户

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserProfile> getProfile(@CurrentUser Long userId) {
        // userId 自动从 JWT Token 中获取
        UserProfile profile = userService.getProfile(userId);
        return Result.success(profile);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(
            @CurrentUser Long userId,
            @RequestBody UpdateProfileRequest request
    ) {
        userService.updateProfile(userId, request);
        return Result.success();
    }
}
```

### 不需要认证的接口

```java
@RestController
@RequestMapping("/api/public")
public class PublicController {

    /**
     * 公开接口 - 不需要认证
     * 访问路径为 /api/public/**，已配置为 permitAll
     */
    @GetMapping("/info")
    public Result<PublicInfo> getPublicInfo() {
        return Result.success(publicInfoService.getInfo());
    }
}
```

### 基于角色的访问控制

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * 仅管理员可访问
     * 需要 ROLE_ADMIN 角色
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public Result<List<User>> listUsers() {
        return Result.success(userService.listAllUsers());
    }

    /**
     * 管理员或普通用户可访问
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/settings")
    public Result<Settings> getSettings() {
        return Result.success(settingsService.getSettings());
    }
}
```

## 安全配置说明

### 公开路径（无需认证）

以下路径默认公开，无需 JWT Token：

- `/api/auth/**` - 认证相关接口
- `/api/public/**` - 公开接口
- `/actuator/**` - 监控接口
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - API 文档
- `/error` - 错误页面

### 受保护路径（需要认证）

除上述路径外，所有其他接口都需要有效的 JWT Token。

## Token 生命周期管理

### Token 类型

| Token 类型 | 过期时间 | 用途 |
|-----------|---------|------|
| Access Token | 24小时 | 访问受保护接口 |
| Refresh Token | 7天 | 刷新 Access Token |

### Token 刷新策略

1. **客户端检测 Access Token 即将过期**
   - 前端在 Access Token 过期前 5-10 分钟主动刷新

2. **使用 Refresh Token 获取新 Token**
   ```bash
   POST /api/auth/refresh
   {
     "refreshToken": "old-refresh-token"
   }
   ```

3. **更新本地存储的 Token**
   - 替换旧的 Access Token
   - 保存新的 Refresh Token（如果返回了新的）

## 安全最佳实践

### 1. 密钥管理

- ✅ 使用强密钥（至少 256 位）
- ✅ 生产环境使用环境变量配置
- ✅ 定期轮换密钥
- ❌ 不要在代码中硬编码密钥
- ❌ 不要使用默认密钥

### 2. Token 存储

- ✅ Access Token 存储在内存或 SessionStorage
- ✅ Refresh Token 存储在 HttpOnly Cookie
- ❌ 不要将 Token 存储在 LocalStorage（XSS 风险）
- ❌ 不要在 URL 中传递 Token

### 3. HTTPS 传输

- ✅ 生产环境必须使用 HTTPS
- ✅ 启用 HSTS
- ❌ 不要在 HTTP 环境下传输 Token

### 4. 过期时间

- ✅ Access Token 设置合理的过期时间（1-24 小时）
- ✅ Refresh Token 设置较长的过期时间（7-30 天）
- ✅ 实现单点登出功能

### 5. 错误处理

- ✅ 统一处理认证异常
- ✅ 返回友好的错误信息
- ✅ 记录安全相关日志

## 常见问题

### 1. Token 无效

**问题**: 请求返回 401 Unauthorized

**原因**:
- Token 已过期
- Token 格式错误
- Token 签名验证失败

**解决方案**:
- 检查 Token 是否过期
- 使用 Refresh Token 刷新
- 重新登录获取新 Token

### 2. CORS 错误

**问题**: 前端请求时出现 CORS 错误

**原因**:
- 前端域名未在 CORS 配置中

**解决方案**:
- 已在 `SecurityConfig` 中配置允许所有域名
- 确保前端携带正确的请求头

### 3. @CurrentUser 注解无法获取用户

**问题**: Controller 方法中使用 @CurrentUser 时返回 null

**原因**:
- 用户未认证
- 请求未携带 Token

**解决方案**:
- 确保请求携带有效的 Token
- 检查 SecurityConfig 配置

## 部署检查清单

### 开发环境

- [ ] 配置 JWT 密钥
- [ ] 验证登录功能
- [ ] 验证 Token 验证功能
- [ ] 测试刷新 Token

### 生产环境

- [ ] 修改 JWT 密钥为强密钥
- [ ] 配置环境变量
- [ ] 启用 HTTPS
- [ ] 配置 Token 过期时间
- [ ] 测试完整认证流程
- [ ] 配置监控和告警

## 性能优化

### 1. Token 验证缓存

JWT 验证是无状态的，不需要数据库查询，性能较好。

### 2. 减少不必要的验证

在 `JwtAuthenticationFilter.shouldNotFilter()` 中配置不需要验证的路径。

### 3. Token 大小优化

- 仅包含必要的用户信息
- 避免在 Token 中存储大量数据
- 使用高效的序列化方式

## 监控和日志

### 关键指标

1. **Token 生成数量**
2. **Token 验证成功率**
3. **认证失败次数**
4. **Token 刷新频率**

### 日志记录

```java
// 在 JwtAuthenticationFilter 中已配置日志
log.debug("Authenticated user: {}, userId: {}, roles: {}", username, userId, roles);
```

## 相关文档

- [Spring Security 官方文档](https://docs.spring.io/spring-security/reference/)
- [JWT 规范 (RFC 7519)](https://tools.ietf.org/html/rfc7519)

## 更新日志

| 日期 | 版本 | 更新内容 |
|------|------|----------|
| 2026-03-31 | 1.0.0 | 初始版本，完成统一 JWT 认证机制 |

## 总结

统一 JWT 认证机制已集成到 `common-core` 模块，所有微服务都可以直接使用。

**核心特性**:
- ✅ 统一的 Token 生成和验证
- ✅ 自动拦截和验证请求
- ✅ 便捷的 @CurrentUser 注解
- ✅ 灵活的角色权限控制
- ✅ 完善的安全配置

**使用步骤**:
1. 在微服务的 pom.xml 中引入 common-core 依赖
2. 配置 JWT 参数
3. 在 Controller 中使用 @CurrentUser 获取用户信息
4. 在需要认证的接口上使用 @PreAuthorize 注解
