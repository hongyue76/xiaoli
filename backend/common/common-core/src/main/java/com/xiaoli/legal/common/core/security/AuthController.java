package com.xiaoli.legal.common.core.security;

import com.xiaoli.legal.common.core.domain.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器示例
 * 展示如何使用 JWT 认证机制
 *
 * 注意：这是一个示例控制器，实际应用中应该在具体的微服务中实现
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 用户登录（示例）
     * 实际应用中应该验证用户名和密码
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        // TODO: 实际应用中应该验证用户名和密码
        // User user = userService.authenticate(request.getUsername(), request.getPassword());

        // 示例：假设验证成功
        Long userId = 1L;
        String username = request.getUsername();
        String roles = "USER,ADMIN";

        // 生成 Token
        String accessToken = jwtTokenProvider.generateAccessToken(userId, username, roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, username);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(userId);
        response.setUsername(username);
        response.setRoles(List.of(roles.split(",")));

        return Result.success(response);
    }

    /**
     * 刷新 Token（示例）
     */
    @PostMapping("/refresh")
    public Result<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        // 验证 Refresh Token
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            return Result.fail("Invalid refresh token");
        }

        // 从 Refresh Token 中获取用户信息
        String username = jwtTokenProvider.getUsernameFromToken(request.getRefreshToken());
        Long userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());

        // 生成新的 Access Token
        String roles = "USER,ADMIN"; // TODO: 从数据库获取实际角色
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, roles);

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(newAccessToken);

        return Result.success(response);
    }

    /**
     * 登录请求
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 登录响应
     */
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private Long userId;
        private String username;
        private List<String> roles;

        public LoginResponse() {
        }

        public LoginResponse(String accessToken, String refreshToken, Long userId, String username, List<String> roles) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.userId = userId;
            this.username = username;
            this.roles = roles;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }

    /**
     * 刷新 Token 请求
     */
    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    /**
     * 刷新 Token 响应
     */
    public static class RefreshTokenResponse {
        private String accessToken;

        public RefreshTokenResponse() {
        }

        public RefreshTokenResponse(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}