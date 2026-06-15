package com.hkg.distributedstorageiostack.cache;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Engine-owned cache that evicts lower-value semantic classes before critical pages.
 */
public final class SemanticBlockCache {
    private final int capacity;
    private final Map<String, CacheClass> entries = new HashMap<>();

    public SemanticBlockCache(int capacity) {
        this.capacity = capacity;
    }

    public CacheDecision access(String key, CacheClass cacheClass) {
        if (entries.containsKey(key)) {
            entries.put(key, cacheClass);
            return CacheDecision.cacheHit();
        }
        String evicted = "";
        if (entries.size() >= capacity) {
            evicted = entries.entrySet().stream()
                    .min(Comparator.comparingInt(entry -> entry.getValue().priority()))
                    .orElseThrow()
                    .getKey();
            entries.remove(evicted);
        }
        entries.put(key, cacheClass);
        return CacheDecision.cacheMiss(evicted);
    }

    public boolean contains(String key) {
        return entries.containsKey(key);
    }
}
