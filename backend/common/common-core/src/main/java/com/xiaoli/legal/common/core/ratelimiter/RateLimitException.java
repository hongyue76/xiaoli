package com.xiaoli.legal.common.core.ratelimiter;

/**
 * 限流异常
 * 当请求超过限流阈值时抛出
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
public class RateLimitException extends RuntimeException {

    /**
     * 限流提示信息
     */
    private final String message;

    /**
     * 限流键
     */
    private final String key;

    /**
     * 最大请求数
     */
    private final int maxRequests;

    /**
     * 时间窗口
     */
    private final int duration;

    /**
     * 当前请求数
     */
    private final long currentRequests;

    public RateLimitException(
            String message,
            String key,
            int maxRequests,
            int duration,
            long currentRequests
    ) {
        super(message);
        this.message = message;
        this.key = key;
        this.maxRequests = maxRequests;
        this.duration = duration;
        this.currentRequests = currentRequests;
    }

    public String getMessage() {
        return message;
    }

    public String getKey() {
        return key;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public int getDuration() {
        return duration;
    }

    public long getCurrentRequests() {
        return currentRequests;
    }
}
