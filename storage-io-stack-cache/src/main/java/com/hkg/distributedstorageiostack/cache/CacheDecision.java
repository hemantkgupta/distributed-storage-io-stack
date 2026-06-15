package com.hkg.distributedstorageiostack.cache;

/**
 * Cache access result used by recency and semantic cache policies.
 */
public record CacheDecision(boolean hit, String evictedKey) {
    public static CacheDecision cacheHit() {
        return new CacheDecision(true, "");
    }

    public static CacheDecision cacheMiss(String evictedKey) {
        return new CacheDecision(false, evictedKey);
    }
}
