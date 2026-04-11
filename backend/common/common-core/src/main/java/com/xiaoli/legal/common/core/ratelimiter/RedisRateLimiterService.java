package com.xiaoli.legal.common.core.ratelimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis 限流服务
 * 使用 Lua 脚本实现原子性的限流操作
 *
 * 算法: 滑动窗口算法
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
@Service
public class RedisRateLimiterService {

    private static final Logger log = LoggerFactory.getLogger(RedisRateLimiterService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisRateLimiterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Lua 脚本 - 滑动窗口限流
     *
     * 参数:
     * KEYS[1] - 限流键
     * ARGV[1] - 时间窗口（秒）
     * ARGV[2] - 最大请求数
     * ARGV[3] - 当前时间戳（毫秒）
     *
     * 返回:
     * 0 - 允许请求
     * 1 - 拒绝请求（超出限流）
     */
    private static final String RATE_LIMIT_SCRIPT =
            "local key = KEYS[1]\n" +
            "local duration = tonumber(ARGV[1])\n" +
            "local maxRequests = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local windowStart = now - (duration * 1000)\n" +
            "\n" +
            "-- 删除过期的请求记录\n" +
            "redis.call('ZREMRANGEBYSCORE', key, '-inf', windowStart)\n" +
            "\n" +
            "-- 获取当前窗口内的请求数\n" +
            "local current = redis.call('ZCARD', key)\n" +
            "\n" +
            "-- 检查是否超出限流\n" +
            "if current >= maxRequests then\n" +
            "    return {1, current}\n" +
            "end\n" +
            "\n" +
            "-- 添加当前请求\n" +
            "redis.call('ZADD', key, now, now)\n" +
            "redis.call('EXPIRE', key, duration + 1)\n" +
            "\n" +
            "return {0, current + 1}";

    /**
     * Lua 脚本对象
     */
    private final RedisScript<Object> rateLimitScript = new DefaultRedisScript<>(
            RATE_LIMIT_SCRIPT,
            Object.class
    );

    /**
     * 尝试获取限流许可
     *
     * @param key 限流键
     * @param maxRequests 最大请求数
     * @param duration 时间窗口（秒）
     * @return 是否允许请求
     */
    public boolean tryAcquire(String key, int maxRequests, int duration) {
        try {
            long now = System.currentTimeMillis();

            Object result = redisTemplate.execute(
                    rateLimitScript,
                    Collections.singletonList(key),
                    String.valueOf(duration),
                    String.valueOf(maxRequests),
                    String.valueOf(now)
            );

            if (result != null) {
                // 返回结果是一个列表 [allowed, count]
                if (result instanceof java.util.List) {
                    java.util.List<?> list = (java.util.List<?>) result;
                    if (!list.isEmpty()) {
                        Object first = list.get(0);
                        if (first instanceof Long) {
                            return (Long) first == 0;
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Redis rate limit error: key={}, error={}", key, e.getMessage(), e);
            // Redis 异常时，允许请求通过（降级策略）
            return true;
        }
    }

    /**
     * 获取当前窗口内的请求数
     *
     * @param key 限流键
     * @return 当前请求数
     */
    public long getCurrentRequests(String key) {
        try {
            Long count = redisTemplate.opsForZSet().size(key);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("Get current requests error: key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 重置限流（仅用于测试）
     *
     * @param key 限流键
     */
    public void reset(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Reset rate limit error: key={}, error={}", key, e.getMessage(), e);
        }
    }

    /**
     * 生成限流键
     *
     * @param prefix 前缀
     * @param identifier 标识符（IP、用户ID等）
     * @param window 时间窗口
     * @return 限流键
     */
    public String generateKey(String prefix, String identifier, long window) {
        return String.format("%s:%s:%d", prefix, identifier, window);
    }
}
