package com.hkg.distributedstorageiostack.cache;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SemanticBlockCacheTest {
    @Test
    void semanticCacheEvictsScanPageBeforeRootPage() {
        SemanticBlockCache cache = new SemanticBlockCache(2);

        cache.access("root", CacheClass.ROOT);
        cache.access("scan", CacheClass.SCAN);
        CacheDecision decision = cache.access("filter", CacheClass.FILTER);

        assertThat(decision.evictedKey()).isEqualTo("scan");
        assertThat(cache.contains("root")).isTrue();
        assertThat(cache.contains("filter")).isTrue();
    }
}
