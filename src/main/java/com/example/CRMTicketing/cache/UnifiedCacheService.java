package com.example.CRMTicketing.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class UnifiedCacheService {

    public enum CacheType {
        REDIS, CAFFEINE, LRU
    }

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager caffeineCacheManager;
    private final LruCacheManager lruCacheManager;

    public UnifiedCacheService(RedisTemplate<String, Object> redisTemplate,
                               CacheManager caffeineCacheManager,
                               LruCacheManager lruCacheManager) {
        this.redisTemplate = redisTemplate;
        this.caffeineCacheManager = caffeineCacheManager;
        this.lruCacheManager = lruCacheManager;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz, CacheType cacheType) {
        try {
            switch (cacheType) {
                case REDIS:
                    Object val = redisTemplate.opsForValue().get(key);
                    if (val == null) return null;
                    return clazz.isInstance(val) ? (T) val : null;
                case CAFFEINE:
                    Cache cache = caffeineCacheManager.getCache("local");
                    if (cache == null) return null;
                    return cache.get(key, clazz);
                case LRU:
                    return lruCacheManager.get(key, clazz);
                default:
                    return null;
            }
        } catch (Exception e) {
            log.warn("Cache get failed for key {} type {}: {}", key, cacheType, e.getMessage());
            return null;
        }
    }

    public void put(String key, Object value, CacheType cacheType) {
        if (value == null) return;
        try {
            switch (cacheType) {
                case REDIS:
                    redisTemplate.opsForValue().set(key, value);
                    break;
                case CAFFEINE:
                    Cache cache = caffeineCacheManager.getCache("local");
                    if (cache != null) cache.put(key, value);
                    break;
                case LRU:
                    lruCacheManager.put(key, value);
                    break;
            }
        } catch (Exception e) {
            log.warn("Cache put failed for key {} type {}: {}", key, cacheType, e.getMessage());
        }
    }

    public void evict(String key, CacheType cacheType) {
        try {
            switch (cacheType) {
                case REDIS:
                    redisTemplate.delete(key);
                    break;
                case CAFFEINE:
                    Cache cache = caffeineCacheManager.getCache("local");
                    if (cache != null) cache.evict(key);
                    break;
                case LRU:
                    lruCacheManager.evict(key);
                    break;
            }
        } catch (Exception e) {
            log.warn("Cache evict failed for key {} type {}: {}", key, cacheType, e.getMessage());
        }
    }

    public static String ticketKey(Long id) { return "Ticket:id:" + id; }
    public static String ticketListKey() { return "Ticket:list:all"; }
    public static String agentKey(Long id) { return "Agent:id:" + id; }
    public static String commentKey(Long id) { return "Comment:id:" + id; }
}
