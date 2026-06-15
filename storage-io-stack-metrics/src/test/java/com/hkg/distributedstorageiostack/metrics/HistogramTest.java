package com.hkg.distributedstorageiostack.metrics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HistogramTest {
    @Test
    void percentileReturnsTailSample() {
        Histogram histogram = new Histogram();
        histogram.record(10);
        histogram.record(20);
        histogram.record(1_000);

        assertThat(histogram.percentile(0.99)).isEqualTo(1_000);
    }
}
