package com.hkg.distributedstorageiostack.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tiny percentile histogram for fsync and simulated-device latency tests.
 */
public final class Histogram {
    private final List<Long> samples = new ArrayList<>();

    public void record(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("histogram sample must be non-negative");
        }
        samples.add(value);
    }

    public long percentile(double quantile) {
        if (samples.isEmpty()) {
            return 0;
        }
        List<Long> sorted = new ArrayList<>(samples);
        Collections.sort(sorted);
        int index = (int) Math.ceil(quantile * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
    }
}
