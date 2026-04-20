package com.xiaoli.legal.common.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 认证过滤器
 * 拦截请求，验证 JWT Token，设置认证信息
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 从请求中获取 Token
            String jwt = resolveToken(request);

            // 验证 Token 并设置认证信息
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                String roles = jwtTokenProvider.getRolesFromToken(jwt);

                log.debug("Authenticated user: {}, userId: {}, roles: {}", username, userId, roles);

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                getAuthorities(roles)
                        );

                // 设置详细信息
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置到 Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取 Token
     *
     * @param request HttpServletRequest
     * @return JWT Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        return jwtTokenProvider.resolveToken(bearerToken);
    }

    /**
     * 将角色字符串转换为权限列表
     *
     * @param roles 角色字符串（逗号分隔）
     * @return 权限列表
     */
    private List<SimpleGrantedAuthority> getAuthorities(String roles) {
        if (roles != null && !roles.isEmpty()) {
            String[] roleArray = roles.split(",");
            List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
            for (String role : roleArray) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.trim()));
            }
            return authorities;
        }
        return Collections.emptyList();
    }

    /**
     * 判断是否应该过滤该请求
     * 排除不需要认证的路径
     *
     * @param request HttpServletRequest
     * @return 是否过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // 不需要认证的路径
        return path.startsWith("/api/auth/") ||        // 认证相关
               path.startsWith("/api/public/") ||      // 公开接口
               path.startsWith("/api/consult/") ||     // 法律咨询
               path.startsWith("/api/document/") ||    // 文书服务
               path.startsWith("/api/case/") ||        // 案例检索
               path.startsWith("/api/contract/") ||   // 合同审查
               path.startsWith("/api/decision/") ||   // 司法决策
               path.startsWith("/api/compliance/") || // 企业合规
               path.startsWith("/api/speech/") ||     // 语音对话
               path.startsWith("/api/evidence/") ||   // 证据管理
               path.startsWith("/api/analysis/") ||   // 分析服务
               path.startsWith("/actuator/") ||        // 监控接口
               path.startsWith("/swagger") ||         // Swagger
               path.startsWith("/v3/api-docs") ||     // API 文档
               path.startsWith("/webjars/") ||         // Web资源
               path.equals("/error");                   // 错误页面
    }
}
