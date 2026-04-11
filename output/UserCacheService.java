package com.xiaoli.legal.common.core.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户信息缓存服务
 * 缓存用户基本信息，减少重复查�\ufffd
 * 
 * 缓存策略�\ufffd
 * - 缓存时间�\ufffd30 分钟
 * - 缓存内容：用户基本信息�\ufffd�用户权�\ufffd
 * - 更新策略：用户信息更新时清除或更新缓�\ufffd
 */
@Slf4j
@Service
public class UserCacheService {

    /**
     * 根据用户ID获取用户信息（缓�\ufffd 30 分钟�\ufffd
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Cacheable(value = RedisCacheConfig.CACHE_USER, key = "#userId", unless = "#result == null")
    public Object getUserById(Long userId) {
        // TODO: 从数据库查询用户信息
        log.debug("从数据库查询用户信息，userId: {}", userId);
        return null;
    }

    /**
     * 根据用户名获取用户信息（缓存 30 分钟�\ufffd
     *
     * @param username 用户�\ufffd
     * @return 用户信息
     */
    @Cacheable(value = RedisCacheConfig.CACHE_USER, key = "'username:' + #username", unless = "#result == null")
    public Object getUserByUsername(String username) {
        // TODO: 从数据库查询用户信息
        log.debug("从数据库查询用户信息，username: {}", username);
        return null;
    }

    /**
     * 根据手机号获取用户信息（缓存 30 分钟�\ufffd
     *
     * @param phone 手机�\ufffd
     * @return 用户信息
     */
    @Cacheable(value = RedisCacheConfig.CACHE_USER, key = "'phone:' + #phone", unless = "#result == null")
    public Object getUserByPhone(String phone) {
        // TODO: 从数据库查询用户信息
        log.debug("从数据库查询用户信息，phone: {}", phone);
        return null;
    }

    /**
     * 根据邮箱获取用户信息（缓�\ufffd 30 分钟�\ufffd
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Cacheable(value = RedisCacheConfig.CACHE_USER, key = "'email:' + #email", unless = "#result == null")
    public Object getUserByEmail(String email) {
        // TODO: 从数据库查询用户信息
        log.debug("从数据库查询用户信息，email: {}", email);
        return null;
    }

    /**
     * 获取用户权限（缓�\ufffd 30 分钟�\ufffd
     *
     * @param userId 用户ID
     * @return 用户权限列表
     */
    @Cacheable(value = RedisCacheConfig.CACHE_USER, key = "'permissions:' + #userId", unless = "#result == null || #result.isEmpty()")
    public Object getUserPermissions(Long userId) {
        // TODO: 查询用户权限
        log.debug("查询用户权限，userId: {}", userId);
        return null;
    }

    /**
     * 更新用户信息（更新缓存）
     *
     * @param userId 用户ID
     * @param userInfo 用户信息
     * @return 更新后的用户信息
     */
    @CachePut(value = RedisCacheConfig.CACHE_USER, key = "#userId")
    public Object updateUser(Long userId, Object userInfo) {
        // TODO: 更新用户信息
        log.debug("更新用户信息并更新缓存，userId: {}", userId);
        return userInfo;
    }

    /**
     * 删除用户（清除缓存）
     *
     * @param userId 用户ID
     */
    @CacheEvict(value = RedisCacheConfig.CACHE_USER, key = "#userId")
    public void deleteUser(Long userId) {
        // TODO: 删除用户
        log.debug("删除用户并清除缓存，userId: {}", userId);
    }

    /**
     * 清除用户�\ufffd有缓�\ufffd
     *
     * @param userId 用户ID
     */
    @CacheEvict(value = RedisCacheConfig.CACHE_USER, allEntries = true)
    public void clearAllUserCache() {
        log.debug("清除�\ufffd有用户缓�\ufffd");
    }
}
