package com.hkg.distributedstorageiostack.cache;

/**
 * Semantic classes that a database cache can prioritize better than pure recency.
 */
public enum CacheClass {
    ROOT(100),
    INDEX(80),
    FILTER(70),
    DATA(40),
    SCAN(10);

    private final int priority;

    CacheClass(int priority) {
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }
}
