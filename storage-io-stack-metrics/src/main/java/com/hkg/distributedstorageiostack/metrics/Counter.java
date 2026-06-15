package com.hkg.distributedstorageiostack.metrics;

/**
 * Small monotonic counter for lab metrics.
 */
public final class Counter {
    private long value;

    public void add(long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("counter delta must be non-negative");
        }
        value += delta;
    }

    public long value() {
        return value;
    }
}
