package com.hkg.distributedstorageiostack.common;

/**
 * Simulated latency returned by device and commit-path labs.
 */
public record LatencySample(long micros, String reason) {
    public LatencySample {
        if (micros < 0) {
            throw new IllegalArgumentException("micros must be non-negative");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
    }
}
