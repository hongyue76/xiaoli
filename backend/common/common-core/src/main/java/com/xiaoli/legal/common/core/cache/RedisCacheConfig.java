package com.xiaoli.legal.common.core.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 缓存策略配置
 * 
 * 缓存时间策略：
 * - AI 对话历史: 7 天
 * - 常用法条: 24 小时
 * - 用户信息: 30 分钟
 * - 案例检索结果: 1 小时
 * - 其他默认: 30 分钟
 */
@Configuration
@EnableCaching
public class RedisCacheConfig implements CachingConfigurer {

    /**
     * 缓存名称常量
     */
    public static final String CACHE_CONVERSATION = "conversation";          // 会话缓存
    public static final String CACHE_CONVERSATION_MESSAGE = "conversation_message";  // 会话消息缓存
    public static final String CACHE_USER = "user";                          // 用户信息缓存
    public static final String CACHE_LAW_ARTICLE = "law_article";            // 法条缓存
    public static final String CACHE_CASE_SEARCH = "case_search";            // 案例检索缓存
    public static final String CACHE_DOCUMENT = "document";                 // 文书缓存
    public static final String CACHE_CONTRACT = "contract";                 // 合同缓存
    public static final String CACHE_EVIDENCE = "evidence";                  // 证据缓存
    public static final String CACHE_ANALYSIS = "analysis";                  // 分析缓存

    /**
     * 缓存时间配置（单位：秒）
     */
    private static final Duration CACHE_CONVERSATION_TTL = Duration.ofDays(7);      // 7 天
    private static final Duration CACHE_MESSAGE_TTL = Duration.ofDays(7);           // 7 天
    private static final Duration CACHE_USER_TTL = Duration.ofMinutes(30);         // 30 分钟
    private static final Duration CACHE_LAW_ARTICLE_TTL = Duration.ofHours(24);     // 24 小时
    private static final Duration CACHE_CASE_SEARCH_TTL = Duration.ofHours(1);       // 1 小时
    private static final Duration CACHE_DEFAULT_TTL = Duration.ofMinutes(30);       // 30 分钟

    /**
     * 配置 CacheManager
     * 支持不同缓存名称使用不同的过期时间
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(CACHE_DEFAULT_TTL)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();  // 不缓存空值

        // 针对不同缓存的个性化配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // AI 对话历史缓存 - 7 天
        cacheConfigurations.put(CACHE_CONVERSATION, defaultConfig.entryTtl(CACHE_CONVERSATION_TTL));
        cacheConfigurations.put(CACHE_CONVERSATION_MESSAGE, defaultConfig.entryTtl(CACHE_MESSAGE_TTL));
        
        // 用户信息缓存 - 30 分钟
        cacheConfigurations.put(CACHE_USER, defaultConfig.entryTtl(CACHE_USER_TTL));
        
        // 常用法条缓存 - 24 小时
        cacheConfigurations.put(CACHE_LAW_ARTICLE, defaultConfig.entryTtl(CACHE_LAW_ARTICLE_TTL));
        
        // 案例检索结果缓存 - 1 小时
        cacheConfigurations.put(CACHE_CASE_SEARCH, defaultConfig.entryTtl(CACHE_CASE_SEARCH_TTL));
        
        // 其他缓存使用默认配置
        cacheConfigurations.put(CACHE_DOCUMENT, defaultConfig);
        cacheConfigurations.put(CACHE_CONTRACT, defaultConfig);
        cacheConfigurations.put(CACHE_EVIDENCE, defaultConfig);
        cacheConfigurations.put(CACHE_ANALYSIS, defaultConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()  // 支持事务
                .build();
    }

    /**
     * 自定义 Key 生成器
     * 格式: 缓存名称:方法名:参数值
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName());
            sb.append(":");
            sb.append(method.getName());
            sb.append(":");
            for (Object param : params) {
                if (param != null) {
                    sb.append(param.toString());
                    sb.append(":");
                }
            }
            return sb.substring(0, sb.length() - 1);  // 去掉最后的冒号
        };
    }

    /**
     * 缓存异常处理
     * 缓存异常不影响业务流程
     */
    @Bean
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    /**
     * RedisTemplate 配置
     * 用于 RedisCacheUtils 和其他需要直接操作 Redis 的组件
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用 String 序列化 key
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // 使用 JSON 序列化 value
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * StringRedisTemplate 配置
     * 用于简单的字符串操作
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
