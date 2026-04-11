package com.xiaoli.legal.common.core.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户认证信息
 * 实现 Spring Security UserDetails 接口
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
public class UserPrincipal implements UserDetails {

    private Long userId;
    private String username;
    private String password;
    private List<String> roles;

    public UserPrincipal() {
    }

    public UserPrincipal(Long userId, String username, String password, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 从用户信息创建 UserPrincipal
     */
    public static UserPrincipal create(Long userId, String username, String password, List<String> roles) {
        UserPrincipal principal = new UserPrincipal();
        principal.setUserId(userId);
        principal.setUsername(username);
        principal.setPassword(password);
        principal.setRoles(roles);
        return principal;
    }

    /**
     * 从用户信息创建 UserPrincipal（无密码）
     */
    public static UserPrincipal create(Long userId, String username, List<String> roles) {
        UserPrincipal principal = new UserPrincipal();
        principal.setUserId(userId);
        principal.setUsername(username);
        principal.setPassword(null);
        principal.setRoles(roles);
        return principal;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}