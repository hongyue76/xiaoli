package com.xiaoli.legal.common.core.ratelimiter;

import java.lang.annotation.*;

/**
 * 限流注解
 * 基于 Redis + Lua 实现接口限流
 *
 * 使用示例:
 * <pre>
 * &#64;RateLimiter(value = "api:consult", maxRequests = 100, duration = 60)
 * &#64;PostMapping("/consult")
 * public Result consult(@RequestBody ConsultRequest request) {
 *     // 业务逻辑
 * }
 * </pre>
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流键前缀
     * 最终的限流键格式: {value}:{identifier}:{window}
     * 例如: api:consult:127.0.0.1:1711881600
     */
    String value() default "api:default";

    /**
     * 最大请求数
     */
    int maxRequests() default 100;

    /**
     * 时间窗口（秒）
     */
    int duration() default 60;

    /**
     * 限流标识符
     * 默认使用 IP 地址
     * 可以自定义，如用户ID、用户名等
     */
    String identifier() default "";

    /**
     * 限流提示信息
     */
    String message() default "请求过于频繁，请稍后再试";
}
