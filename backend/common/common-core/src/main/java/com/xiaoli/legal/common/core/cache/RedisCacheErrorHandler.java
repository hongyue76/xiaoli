package com.xiaoli.legal.common.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * Redis 缓存异常处理器
 * 缓存异常不影响业务流程，记录日志后继续执行
 */
public class RedisCacheErrorHandler implements CacheErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.error("Redis 缓存获取异常 - Cache: {}, Key: {}, 异常: {}", 
                cache.getName(), key, exception.getMessage(), exception);
        // 不抛出异常，继续从数据库查询
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("Redis 缓存写入异常 - Cache: {}, Key: {}, 异常: {}", 
                cache.getName(), key, exception.getMessage(), exception);
        // 不抛出异常，继续执行业务逻辑
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Redis 缓存清除异常 - Cache: {}, Key: {}, 异常: {}", 
                cache.getName(), key, exception.getMessage(), exception);
        // 不抛出异常，继续执行业务逻辑
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Redis 缓存清空异常 - Cache: {}, 异常: {}", 
                cache.getName(), exception.getMessage(), exception);
        // 不抛出异常，继续执行业务逻辑
    }
}
