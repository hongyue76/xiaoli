package com.xiaoli.legal.common.core.security;

import java.lang.annotation.*;

/**
 * 当前用户注解
 * 用于在 Controller 方法参数中自动注入当前登录用户信息
 *
 * 使用示例:
 * <pre>
 * &#64;GetMapping("/profile")
 * public Result getProfile(@CurrentUser Long userId) {
 *     // userId 会自动从 Security Context 中获取
 * }
 * </pre>
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
