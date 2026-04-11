# JWT 认证快速开始

## 概述

本文档提供 JWT 认证机制的快速集成和使用方法。

## 快速集成（3 步）

### 步骤 1: 添加依赖（已完成）

`common-core` 模块已包含所有必需的依赖：
- Spring Security
- JWT (jjwt)
- 其他核心依赖

### 步骤 2: 配置 JWT 参数

在微服务的 `application.yml` 中添加配置：

```yaml
# JWT 配置
jwt:
  secret: ${JWT_SECRET:xiaoli-legal-secret-key-2024-for-jwt-token-generation}
  expiration: ${JWT_EXPIRATION:86400000}
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000}
```

### 步骤 3: 使用 @CurrentUser 注解

在 Controller 中直接使用：

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public Result<UserProfile> getProfile(@CurrentUser Long userId) {
        // userId 自动从 Token 中获取
        return Result.success(userService.getProfile(userId));
    }
}
```

## 测试认证功能

### 1. 登录获取 Token

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

响应：
```json
{
  "code": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlcyI6IlVTRVIsQURNSU4ifQ.xxx",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4ifQ.xxx",
    "userId": 1,
    "username": "admin",
    "roles": ["USER", "ADMIN"]
  }
}
```

### 2. 使用 Token 访问受保护接口

```bash
curl -X GET http://localhost:8081/api/user/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 3. 刷新 Token

```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
  }'
```

## 常用代码片段

### 获取当前用户信息

```java
// 方式 1: 使用 @CurrentUser 注解
@GetMapping("/info")
public Result<UserInfo> getInfo(@CurrentUser Long userId) {
    return Result.success(userService.getInfo(userId));
}

// 方式 2: 从 SecurityContext 获取
@GetMapping("/info")
public Result<UserInfo> getInfo() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Long userId = (Long) auth.getPrincipal();
    return Result.success(userService.getInfo(userId));
}
```

### 角色权限控制

```java
// 需要管理员角色
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/users/{id}")
public Result<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return Result.success();
}

// 需要管理员或用户角色
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@GetMapping("/data")
public Result<Data> getData() {
    return Result.success(dataService.getData());
}

// 自定义权限
@PreAuthorize("@securityService.hasPermission(#userId, 'READ')")
@GetMapping("/data/{userId}")
public Result<Data> getUserData(@PathVariable Long userId) {
    return Result.success(dataService.getUserData(userId));
}
```

### 无需认证的接口

```java
// 访问路径为 /api/public/**，已配置为公开
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        return Result.success(announcementService.getAll());
    }
}
```

## 配置说明

### 环境变量

```bash
# JWT 密钥（生产环境必须修改）
export JWT_SECRET="your-production-secret-key-min-256-bits"

# Token 过期时间（毫秒）
export JWT_EXPIRATION="86400000"              # 24小时
export JWT_REFRESH_EXPIRATION="604800000"    # 7天
```

### application.yml

```yaml
jwt:
  secret: ${JWT_SECRET:xiaoli-legal-secret-key-2024}
  expiration: ${JWT_EXPIRATION:86400000}
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000}
```

## 错误处理

### 认证失败（401）

```json
{
  "code": 401,
  "message": "Unauthorized",
  "data": null
}
```

**原因**:
- Token 无效或过期
- 未携带 Token

**解决方案**:
- 使用 Refresh Token 刷新
- 重新登录

### 权限不足（403）

```json
{
  "code": 403,
  "message": "Forbidden",
  "data": null
}
```

**原因**:
- 用户角色权限不足

**解决方案**:
- 检查 @PreAuthorize 配置
- 联系管理员分配权限

## 安全建议

### 生产环境必做

1. ✅ 修改 JWT 密钥
   ```yaml
   jwt:
     secret: ${JWT_SECRET}  # 从环境变量读取
   ```

2. ✅ 启用 HTTPS

3. ✅ 设置合理的过期时间
   ```yaml
   jwt:
     expiration: 3600000  # 1小时
     refresh-expiration: 86400000  # 1天
   ```

4. ✅ 实现单点登出

### 开发环境

可以使用默认配置快速开发：
```yaml
jwt:
  secret: xiaoli-legal-secret-key-2024
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天
```

## 故障排查

### Token 验证失败

**检查清单**:
- [ ] Token 是否在有效期内
- [ ] Token 格式是否正确（Bearer xxx）
- [ ] JWT 密钥是否一致
- [ ] Token 是否被篡改

### 无法获取当前用户

**检查清单**:
- [ ] 请求是否携带 Token
- [ ] Token 是否有效
- [ ] @CurrentUser 注解是否正确使用
- [ ] SecurityMvcConfig 是否已配置

### CORS 错误

**解决方案**:
- 已在 SecurityConfig 中配置允许所有域名
- 确保前端请求包含正确的 Origin

## 相关文档

- [完整使用指南](JWT_AUTHENTICATION_GUIDE.md)
- [Spring Security 文档](https://docs.spring.io/spring-security/reference/)

## 常见问题

**Q: 如何禁用某个接口的认证？**

A: 将接口路径设置为 `/api/public/**` 或在 `SecurityConfig` 中添加到 `permitAll()` 列表。

**Q: 如何在 Service 层获取当前用户？**

A: 从 SecurityContext 获取：
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
Long userId = (Long) auth.getPrincipal();
```

**Q: Token 过期了怎么办？**

A: 使用 Refresh Token 刷新：
```bash
POST /api/auth/refresh
{
  "refreshToken": "xxx"
}
```

**Q: 如何实现单点登出？**

A: 实现一个 Token 黑名单机制，将登出的 Token 加入黑名单。

## 总结

JWT 认证已集成到 `common-core`，所有微服务开箱即用：

1. **配置参数** - 在 application.yml 中配置 JWT
2. **使用注解** - 在 Controller 中使用 @CurrentUser
3. **权限控制** - 使用 @PreAuthorize 控制访问权限

**3 步完成集成**，快速开始使用！
