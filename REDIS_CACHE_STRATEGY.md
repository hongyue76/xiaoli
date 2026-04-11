# Redis 缓存策略文档

## 概述
本系统采用 Redis 作为缓存层，通过合理的缓存策略显著提升系统性能，减少数据库压力。本文档详细说明了缓存的设计、使用和最佳实践。

## 缓存架构

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   应用层        │────▶│   Spring Cache  │────▶│     Redis       │
│  (Service)      │     │  (抽象层)        │     │   (缓存存储)    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                │
                                ▼
                        ┌─────────────────┐
                        │   数据库        │
                        │  (PostgreSQL)   │
                        └─────────────────┘
```

## 缓存策略配置

### 1. 缓存时间策略

| 数据类型 | 缓存名称 | 缓存时间 | 说明 |
|---------|---------|---------|------|
| AI 对话历史 | `conversation` | 7 天 | 减少数据库查询，保留用户对话记录 |
| 会话消息 | `conversation_message` | 7 天 | 配合会话缓存，完整对话内容 |
| 用户信息 | `user` | 30 分钟 | 用户基本信息、权限等 |
| 常用法条 | `law_article` | 24 小时 | 热点数据，法条详情和搜索结果 |
| 案例检索结果 | `case_search` | 1 小时 | 相同 query 复用，减少重复检索 |
| 文书模板 | `document` | 30 分钟 | 文书模板和生成结果 |
| 合同信息 | `contract` | 30 分钟 | 合同详情和审查结果 |
| 证据信息 | `evidence` | 30 分钟 | 证据详情和分析结果 |
| 分析结果 | `analysis` | 30 分钟 | 案件分析结果 |

### 2. 缓存键设计

缓存键格式: `缓存名称:业务标识:具体参数`

示例:
- `conversation:12345` - 会话ID为12345的会话
- `conversation:messages:12345` - 会话12345的消息列表
- `user:1001` - 用户ID为1001的用户信息
- `user:username:admin` - 用户名为admin的用户信息
- `law_article:detail:C001` - 法条ID为C001的法条详情
- `law_article:search:劳动合同` - 搜索关键词"劳动合同"的结果
- `case_search:search:123456:民事纠纷` - 查询hash为123456、类型为"民事纠纷"的案例

### 3. 缓存值序列化

- **Key**: String 序列化
- **Value**: JSON 序列化 (GenericJackson2JsonRedisSerializer)
- **Null 值**: 不缓存（disableCachingNullValues）

## 缓存注解使用

### @Cacheable - 读取缓存

**作用**: 方法执行前先查询缓存，缓存中有则直接返回，没有则执行方法并将结果存入缓存

**示例**:
```java
@Cacheable(value = RedisCacheConfig.CACHE_CONVERSATION, 
           key = "#conversationId", 
           unless = "#result == null")
public ConsultRecordVO getConversationDetail(Long conversationId) {
    // 从数据库查询
    return conversationMapper.selectById(conversationId);
}
```

**参数说明**:
- `value`: 缓存名称
- `key`: SpEL 表达式，指定缓存键
- `unless`: 满足条件时不缓存

### @CachePut - 更新缓存

**作用**: 每次都执行方法，并将结果存入缓存

**示例**:
```java
@CachePut(value = RedisCacheConfig.CACHE_CONVERSATION, key = "#result.id")
public ConsultConversation createConversation(ConsultConversation conversation) {
    conversationMapper.insert(conversation);
    return conversation;  // 返回值会被缓存
}
```

**使用场景**: 创建/更新操作后需要更新缓存

### @CacheEvict - 清除缓存

**作用**: 清除指定缓存

**示例**:
```java
// 清除指定键的缓存
@CacheEvict(value = RedisCacheConfig.CACHE_CONVERSATION, key = "#conversationId")
public void closeConversation(Long conversationId) {
    // 更新数据库
    conversation.setStatus("CLOSED");
    conversationMapper.updateById(conversation);
}

// 清除所有缓存
@CacheEvict(value = RedisCacheConfig.CACHE_CONVERSATION, allEntries = true)
public void clearAllConversations() {
    // 清除所有会话缓存
}
```

### @Caching - 组合缓存操作

**作用**: 组合多个缓存操作

**示例**:
```java
@Caching(
    cacheable = @Cacheable(value = CACHE_USER, key = "#userId"),
    evict = @CacheEvict(value = CACHE_USER_PERMISSIONS, key = "'permissions:' + #userId")
)
public Object getUserWithPermissionClear(Long userId) {
    return userMapper.selectById(userId);
}
```

## 缓存服务实现

### 1. 用户信息缓存服务

```java
@Service
public class UserCacheService {
    
    // 获取用户信息（30分钟缓存）
    @Cacheable(value = RedisCacheConfig.CACHE_USER, 
               key = "#userId", 
               unless = "#result == null")
    public Object getUserById(Long userId) {
        return userMapper.selectById(userId);
    }
    
    // 更新用户信息
    @CachePut(value = RedisCacheConfig.CACHE_USER, key = "#userId")
    public Object updateUser(Long userId, Object userInfo) {
        userMapper.updateById(userInfo);
        return userInfo;
    }
    
    // 删除用户
    @CacheEvict(value = RedisCacheConfig.CACHE_USER, key = "#userId")
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }
}
```

### 2. AI 对话缓存服务

```java
@Service
public class ConsultService {
    
    // 获取会话详情（7天缓存）
    @Cacheable(value = RedisCacheConfig.CACHE_CONVERSATION, 
               key = "#conversationId", 
               unless = "#result == null")
    public ConsultRecordVO getConversationDetail(Long conversationId) {
        return convertToVO(conversationMapper.selectById(conversationId));
    }
    
    // 获取会话消息（7天缓存）
    @Cacheable(value = RedisCacheConfig.CACHE_CONVERSATION_MESSAGE, 
               key = "#conversationId", 
               unless = "#result == null || #result.isEmpty()")
    public List<ConsultMessage> getConversationMessages(Long conversationId) {
        return messageMapper.selectList(wrapper);
    }
    
    // 创建会话（更新缓存）
    @CachePut(value = RedisCacheConfig.CACHE_CONVERSATION, key = "#result.id")
    public ConsultConversation createConversation(ConsultConversation conversation) {
        conversationMapper.insert(conversation);
        return conversation;
    }
    
    // 关闭会话（清除缓存）
    @CacheEvict(value = RedisCacheConfig.CACHE_CONVERSATION, key = "#conversationId")
    public void closeConversation(Long conversationId) {
        conversation.setStatus("CLOSED");
        conversationMapper.updateById(conversation);
    }
}
```

### 3. 法条缓存服务

```java
@Service
public class LawArticleCacheService {
    
    // 获取法条详情（24小时缓存）
    @Cacheable(value = RedisCacheConfig.CACHE_LAW_ARTICLE, 
               key = "'detail:' + #articleId", 
               unless = "#result == null")
    public Object getArticleById(String articleId) {
        return lawArticleMapper.selectById(articleId);
    }
    
    // 搜索法条（24小时缓存）
    @Cacheable(value = RedisCacheConfig.CACHE_LAW_ARTICLE, 
               key = "'search:' + #keyword", 
               unless = "#result == null || #result.isEmpty()")
    public List<Object> searchArticles(String keyword) {
        return lawArticleMapper.search(keyword);
    }
    
    // 更新法条（清除缓存）
    @CacheEvict(value = RedisCacheConfig.CACHE_LAW_ARTICLE, 
                key = "'detail:' + #articleId")
    public void updateArticle(String articleId) {
        lawArticleMapper.updateById(article);
    }
}
```

### 4. 案例检索缓存服务

```java
@Service
public class CaseSearchCacheService {
    
    // 案例检索（1小时缓存）
    @Cacheable(value = RedisCacheConfig.CACHE_CASE_SEARCH, 
               key = "'search:' + #query.hashCode() + ':' + #caseType", 
               unless = "#result == null || #result.isEmpty()")
    public List<Object> searchCases(String query, String caseType) {
        return caseSearchService.search(query, caseType);
    }
    
    // 相似案例（1小时缓存）
    @Cacheable(value = RedisCacheConfig.CACHE_CASE_SEARCH, 
               key = "'similar:' + #caseId + ':' + #similarityThreshold", 
               unless = "#result == null || #result.isEmpty()")
    public List<Object> getSimilarCases(String caseId, double similarityThreshold) {
        return caseSearchService.getSimilarCases(caseId, similarityThreshold);
    }
    
    // 清除检索缓存
    @CacheEvict(value = RedisCacheConfig.CACHE_CASE_SEARCH, 
                key = "'search:' + #query.hashCode() + ':' + #caseType")
    public void evictSearchCache(String query, String caseType) {
        // 定时清除或手动清除
    }
}
```

## 工具类使用

### RedisCacheUtils 工具类

提供更灵活的缓存操作：

```java
@Autowired
private RedisCacheUtils redisCacheUtils;

// 设置缓存
redisCacheUtils.set("key", value);
redisCacheUtils.set("key", value, 30, TimeUnit.MINUTES);

// 获取缓存
Object value = redisCacheUtils.get("key");

// 删除缓存
redisCacheUtils.delete("key");

// 批量删除
List<String> keys = Arrays.asList("key1", "key2", "key3");
redisCacheUtils.delete(keys);

// 判断缓存是否存在
Boolean exists = redisCacheUtils.hasKey("key");

// 设置过期时间
redisCacheUtils.expire("key", 30, TimeUnit.MINUTES);

// 获取过期时间
Long ttl = redisCacheUtils.getExpire("key", TimeUnit.SECONDS);

// 获取所有匹配的键
Set<String> keys = redisCacheUtils.keys("user:*");
```

### 专用缓存方法

```java
// 缓存 AI 对话历史（7天）
redisCacheUtils.cacheConversation(conversationId, conversation);
Object conversation = redisCacheUtils.getCachedConversation(conversationId);

// 缓存会话消息（7天）
redisCacheUtils.cacheConversationMessages(conversationId, messages);
Object messages = redisCacheUtils.getCachedConversationMessages(conversationId);

// 缓存用户信息（30分钟）
redisCacheUtils.cacheUserInfo(userId, userInfo);
Object userInfo = redisCacheUtils.getCachedUserInfo(userId);

// 缓存法条（24小时）
redisCacheUtils.cacheLawArticle(articleId, article);
Object article = redisCacheUtils.getCachedLawArticle(articleId);

// 缓存案例检索结果（1小时）
redisCacheUtils.cacheCaseSearchResult(query, result);
Object result = redisCacheUtils.getCachedCaseSearchResult(query);

// 清除用户缓存
redisCacheUtils.clearUserCache(userId);

// 清除会话缓存
redisCacheUtils.clearConversationCache(conversationId);
```

## 缓存更新策略

### 1. Cache-Aside Pattern（旁路缓存）

**读取流程**:
```
1. 查询缓存
2. 缓存命中 → 返回数据
3. 缓存未命中 → 查询数据库
4. 写入缓存
5. 返回数据
```

**更新流程**:
```
1. 更新数据库
2. 删除缓存
```

**示例**:
```java
@CacheEvict(value = CACHE_USER, key = "#userId")
public void updateUser(Long userId, User user) {
    userMapper.updateById(user);
    // 缓存已自动清除，下次查询会重新加载
}
```

### 2. Write-Through Pattern（写穿）

**更新流程**:
```
1. 写入缓存
2. 同步写入数据库
```

**示例**:
```java
@CachePut(value = CACHE_USER, key = "#userId")
public User updateUser(Long userId, User user) {
    userMapper.updateById(user);
    return user;  // 返回值会写入缓存
}
```

### 3. Write-Behind Pattern（写回/异步写）

**更新流程**:
```
1. 写入缓存
2. 异步写入数据库
```

**适用场景**: 高并发写入场景

## 缓存一致性

### 1. 更新数据时清除缓存

```java
@CacheEvict(value = CACHE_USER, key = "#userId")
public void updateUser(Long userId, User user) {
    userMapper.updateById(user);
}
```

### 2. 更新数据时更新缓存

```java
@CachePut(value = CACHE_USER, key = "#userId")
public User updateUser(Long userId, User user) {
    userMapper.updateById(user);
    return user;
}
```

### 3. 批量操作清除相关缓存

```java
@Caching(evict = {
    @CacheEvict(value = CACHE_USER, key = "#userId"),
    @CacheEvict(value = CACHE_USER_PERMISSIONS, key = "'permissions:' + #userId"),
    @CacheEvict(value = CACHE_USER_ROLES, key = "'roles:' + #userId")
})
public void updateUserWithRelatedClear(Long userId, User user) {
    userMapper.updateById(user);
    // 清除用户相关的所有缓存
}
```

## 缓存监控

### 1. Spring Boot Actuator 监控

**启用监控**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,cache,info
  metrics:
    export:
      prometheus:
        enabled: true
```

**监控端点**:
```
GET /actuator/caches              # 查看所有缓存
GET /actuator/caches/{cache}      # 查看指定缓存详情
GET /actuator/metrics/cache.*     # 缓存指标
```

### 2. Redis 命令行监控

```bash
# 查看所有键
redis-cli KEYS "*"

# 查看键的数量
redis-cli DBSIZE

# 查看内存使用情况
redis-cli INFO memory

# 查看缓存命中率
redis-cli INFO stats | grep keyspace

# 查看特定缓存的键
redis-cli KEYS "conversation:*"
redis-cli KEYS "user:*"
redis-cli KEYS "law_article:*"
redis-cli KEYS "case_search:*"
```

### 3. 监控指标

**关键指标**:
- `cache.gets` - 缓存获取次数
- `cache.hits` - 缓存命中次数
- `cache.misses` - 缓存未命中次数
- `cache.hit.ratio` - 缓存命中率 (hits / (hits + misses))
- `cache.puts` - 缓存写入次数
- `cache.evictions` - 缓存驱逐次数
- `cache.size` - 缓存大小

**目标指标**:
- 缓存命中率 > 80%
- 缓存未命中后数据库查询时间 < 100ms
- 缓存写入延迟 < 10ms

## 性能优化

### 1. 缓存预热

系统启动时预先加载热点数据：

```java
@Component
public class CacheWarmUp implements ApplicationRunner {
    
    @Autowired
    private UserCacheService userCacheService;
    
    @Autowired
    private LawArticleCacheService lawArticleCacheService;
    
    @Override
    public void run(ApplicationArguments args) {
        // 预热常用用户
        userCacheService.getUserById(1L);
        userCacheService.getUserByUsername("admin");
        
        // 预热常用法条
        lawArticleCacheService.getArticleByNo("合同法第一条");
        lawArticleCacheService.getArticleByNo("劳动法第三十九条");
    }
}
```

### 2. 批量查询缓存

```java
// 使用多线程批量查询缓存
public Map<Long, Object> batchGetUsers(List<Long> userIds) {
    return userIds.parallelStream()
        .collect(Collectors.toMap(
            userId -> userId,
            userId -> userCacheService.getUserById(userId)
        ));
}
```

### 3. 缓存穿透防护

```java
@Cacheable(value = CACHE_USER, 
           key = "#userId", 
           unless = "#result == null")
public Object getUserById(Long userId) {
    User user = userMapper.selectById(userId);
    if (user == null) {
        // 返回空对象而不是 null，防止缓存穿透
        return Optional.empty();
    }
    return user;
}
```

### 4. 缓存雪崩防护

```java
@Configuration
public class RedisCacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 为不同的缓存设置不同的过期时间，避免同时失效
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("conversation", 
                   RedisCacheConfiguration.defaultCacheConfig()
                       .entryTtl(Duration.ofDays(7)));
        configs.put("user", 
                   RedisCacheConfiguration.defaultCacheConfig()
                       .entryTtl(Duration.ofMinutes(30))
                       .entryTtl(Duration.ofMinutes(30 + (long)(Math.random() * 10))));  // 随机过期时间
        // ...
    }
}
```

### 5. 缓存击穿防护

使用 Redis 分布式锁：

```java
@Autowired
private RedissonClient redissonClient;

public Object getUserWithLock(Long userId) {
    String lockKey = "lock:user:" + userId;
    RLock lock = redissonClient.getLock(lockKey);
    
    try {
        lock.lock(10, TimeUnit.SECONDS);
        return userCacheService.getUserById(userId);
    } finally {
        lock.unlock();
    }
}
```

## 故障处理

### 1. 缓存异常处理

缓存异常不影响业务流程，记录日志后继续执行：

```java
@Component
public class RedisCacheErrorHandler implements CacheErrorHandler {
    
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
        log.error("缓存获取异常 - Cache: {}, Key: {}", cache.getName(), key, e);
        // 不抛出异常，继续从数据库查询
    }
    
    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
        log.error("缓存写入异常 - Cache: {}, Key: {}", cache.getName(), key, e);
        // 不抛出异常，继续执行业务逻辑
    }
}
```

### 2. Redis 宕机降级

```java
@HystrixCommand(fallbackMethod = "getUserFromDb")
@Cacheable(value = CACHE_USER, key = "#userId")
public Object getUserById(Long userId) {
    return userMapper.selectById(userId);
}

// 降级方法：直接从数据库查询
public Object getUserFromDb(Long userId) {
    log.warn("Redis 不可用，从数据库查询用户，userId: {}", userId);
    return userMapper.selectById(userId);
}
```

### 3. 缓存雪崩处理

```java
// 使用多级缓存
@Cacheable(value = "user_local", key = "#userId")  // 本地缓存
public Object getUserById(Long userId) {
    return getCachedUserFromRedis(userId);
}

@Cacheable(value = CACHE_USER, key = "#userId")  // Redis 缓存
public Object getCachedUserFromRedis(Long userId) {
    return userMapper.selectById(userId);
}
```

## 最佳实践

### 1. DO's（推荐做法）

✅ **合理设置缓存时间**
- 热点数据：较长（24小时 - 7天）
- 变化频繁数据：较短（30分钟 - 1小时）
- 用户相关数据：中等（30分钟）

✅ **使用合适的缓存键**
- 包含业务标识
- 避免冲突
- 便于监控和清理

✅ **缓存空值处理**
- 不缓存 null 值
- 使用 Optional 包装
- 防止缓存穿透

✅ **缓存更新策略**
- 更新数据时清除/更新缓存
- 批量操作时清除相关缓存
- 定时刷新热点数据

✅ **监控缓存效果**
- 监控缓存命中率
- 监控缓存大小
- 监控缓存延迟

### 2. DON'Ts（不推荐做法）

❌ **缓存大对象**
- 避免缓存整个列表
- 使用分页缓存
- 使用懒加载

❌ **缓存敏感数据**
- 不缓存密码、密钥
- 加密敏感字段
- 设置合适的权限

❌ **缓存不一致**
- 避免缓存和数据库数据不一致
- 及时更新缓存
- 使用事务保证一致性

❌ **过度缓存**
- 不缓存一次性数据
- 不缓存频繁变化的数据
- 定期清理无用缓存

❌ **缓存雪崩**
- 避免大量缓存同时失效
- 使用随机过期时间
- 使用多级缓存

## 相关文档

- [数据库索引优化](DATABASE_INDEX_OPTIMIZATION.md)
- [连接池优化](CONNECTION_POOL_OPTIMIZATION.md)
- [MyBatis Plus 优化](MYBATIS_PLUS_OPTIMIZATION.md)

## 更新日志

| 日期 | 版本 | 更新内容 |
|------|------|----------|
| 2026-03-31 | 1.0.0 | 初始版本，定义缓存策略和实现 |
