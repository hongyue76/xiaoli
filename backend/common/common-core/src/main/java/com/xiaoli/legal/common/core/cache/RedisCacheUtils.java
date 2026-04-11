package com.xiaoli.legal.common.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具类
 * 提供常用的缓存操作方法
 */
@Component
public class RedisCacheUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存并指定过期时间
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key      缓存键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 获取过期时间
     *
     * @param key      缓存键
     * @param timeUnit 时间单位
     * @return 过期时间
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 获取所有匹配的键
     *
     * @param pattern 键模式
     * @return 键集合
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 缓存 AI 对话历史（7 天）
     */
    public void cacheConversation(String conversationId, Object conversation) {
        set("conversation:" + conversationId, conversation, 7, TimeUnit.DAYS);
    }

    /**
     * 获取 AI 对话历史缓存
     */
    public Object getCachedConversation(String conversationId) {
        return get("conversation:" + conversationId);
    }

    /**
     * 缓存会话消息（7 天）
     */
    public void cacheConversationMessages(String conversationId, Object messages) {
        set("conversation:messages:" + conversationId, messages, 7, TimeUnit.DAYS);
    }

    /**
     * 获取会话消息缓存
     */
    public Object getCachedConversationMessages(String conversationId) {
        return get("conversation:messages:" + conversationId);
    }

    /**
     * 缓存用户信息（30 分钟）
     */
    public void cacheUserInfo(Long userId, Object userInfo) {
        set("user:" + userId, userInfo, 30, TimeUnit.MINUTES);
    }

    /**
     * 获取用户信息缓存
     */
    public Object getCachedUserInfo(Long userId) {
        return get("user:" + userId);
    }

    /**
     * 缓存法条（24 小时）
     */
    public void cacheLawArticle(String articleId, Object article) {
        set("law_article:" + articleId, article, 24, TimeUnit.HOURS);
    }

    /**
     * 获取法条缓存
     */
    public Object getCachedLawArticle(String articleId) {
        return get("law_article:" + articleId);
    }

    /**
     * 缓存案例检索结果（1 小时）
     * key 格式: case_search:hash(query)
     */
    public void cacheCaseSearchResult(String query, Object result) {
        String key = "case_search:" + query.hashCode();
        set(key, result, 1, TimeUnit.HOURS);
    }

    /**
     * 获取案例检索结果缓存
     */
    public Object getCachedCaseSearchResult(String query) {
        String key = "case_search:" + query.hashCode();
        return get(key);
    }

    /**
     * 清除用户相关缓存
     */
    public void clearUserCache(Long userId) {
        delete("user:" + userId);
    }

    /**
     * 清除会话相关缓存
     */
    public void clearConversationCache(String conversationId) {
        delete("conversation:" + conversationId);
        delete("conversation:messages:" + conversationId);
    }

    /**
     * 批量清除用户会话缓存
     */
    public void clearUserConversations(String pattern) {
        Set<String> keys = keys("conversation:" + pattern + "*");
        if (keys != null && !keys.isEmpty()) {
            delete(keys);
        }
    }
}
