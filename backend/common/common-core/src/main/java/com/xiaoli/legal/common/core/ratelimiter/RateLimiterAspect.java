package com.xiaoli.legal.common.core.ratelimiter;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 限流切面
 * 拦截带有 @RateLimiter 注解的方法，执行限流逻辑
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
@Aspect
@Component
public class RateLimiterAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class);

    private final RedisRateLimiterService rateLimiterService;

    public RateLimiterAspect(RedisRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    private static final String DEFAULT_IP_HEADER = "X-Forwarded-For";
    private static final String FALLBACK_IP_HEADER = "X-Real-IP";

    @Around("@annotation(rateLimiterAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimiter rateLimiterAnnotation) throws Throwable {
        // 获取限流配置
        String prefix = rateLimiterAnnotation.value();
        int maxRequests = rateLimiterAnnotation.maxRequests();
        int duration = rateLimiterAnnotation.duration();
        String identifier = rateLimiterAnnotation.identifier();

        // 获取限流标识符
        if (identifier.isEmpty()) {
            identifier = getClientIp();
        }

        // 计算时间窗口（当前时间戳 / duration * duration）
        long window = System.currentTimeMillis() / 1000 / duration * duration;

        // 生成限流键
        String key = rateLimiterService.generateKey(prefix, identifier, window);

        // 尝试获取限流许可
        boolean allowed = rateLimiterService.tryAcquire(key, maxRequests, duration);

        if (!allowed) {
            // 获取当前请求数
            long currentRequests = rateLimiterService.getCurrentRequests(key);

            // 记录限流日志
            log.warn("Rate limit exceeded: key={}, maxRequests={}, duration={}, currentRequests={}",
                    key, maxRequests, duration, currentRequests);

            // 抛出限流异常
            throw new RateLimitException(
                    rateLimiterAnnotation.message(),
                    key,
                    maxRequests,
                    duration,
                    currentRequests
            );
        }

        // 执行原方法
        return joinPoint.proceed();
    }

    /**
     * 获取客户端 IP 地址
     *
     * @return IP 地址
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return "unknown";
            }

            HttpServletRequest request = attributes.getRequest();

            // 优先获取 X-Forwarded-For 头（代理转发）
            String ip = request.getHeader(DEFAULT_IP_HEADER);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 多个代理的情况，取第一个
                int index = ip.indexOf(',');
                if (index != -1) {
                    ip = ip.substring(0, index);
                }
                return ip.trim();
            }

            // 其次获取 X-Real-IP 头
            ip = request.getHeader(FALLBACK_IP_HEADER);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.trim();
            }

            // 最后获取远程地址
            ip = request.getRemoteAddr();
            return ip != null ? ip : "unknown";
        } catch (Exception e) {
            log.error("Get client IP error: {}", e.getMessage(), e);
            return "unknown";
        }
    }
}
