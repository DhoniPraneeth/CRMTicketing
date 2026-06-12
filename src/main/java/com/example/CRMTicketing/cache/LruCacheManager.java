package com.example.CRMTicketing.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCacheManager {

    private final int maxEntries;
    private final Map<String, Object> cache;

    public LruCacheManager(int maxEntries) {
        this.maxEntries = maxEntries;
        this.cache = new LinkedHashMap<>(maxEntries, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                return size() > LruCacheManager.this.maxEntries;
            }
        };
    }

    public synchronized void put(String key, Object value) {
        cache.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(String key, Class<T> clazz) {
        Object v = cache.get(key);
        if (v == null) return null;
        try {
            return (T) v;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public synchronized void evict(String key) {
        cache.remove(key);
    }

    public synchronized void clear() {
        cache.clear();
    }
}
