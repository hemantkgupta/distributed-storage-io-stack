package com.hkg.distributedstorageiostack.device;

import com.hkg.distributedstorageiostack.common.ByteCopies;

import java.util.HashMap;
import java.util.Map;

/**
 * SSD-shaped simulator: no seek arm, but random writes near full capacity trigger GC stalls.
 */
public final class SimulatedSsdDevice implements BlockDevice {
    private final Map<Long, byte[]> blocks = new HashMap<>();
    private final long baseWriteMicros;
    private final long flushMicros;
    private final long gcStallMicros;
    private final long gcAfterRandomWrites;
    private final long capacityBlocks;
    private final double fillThreshold;
    private long lastLba = Long.MIN_VALUE;
    private long randomWrites;
    private long reads;
    private long writes;
    private long flushes;
    private long gcStalls;
    private long simulatedMicros;

    public SimulatedSsdDevice(long baseWriteMicros, long flushMicros, long gcStallMicros, long gcAfterRandomWrites, long capacityBlocks, double fillThreshold) {
        this.baseWriteMicros = baseWriteMicros;
        this.flushMicros = flushMicros;
        this.gcStallMicros = gcStallMicros;
        this.gcAfterRandomWrites = gcAfterRandomWrites;
        this.capacityBlocks = capacityBlocks;
        this.fillThreshold = fillThreshold;
    }

    @Override
    public IoResult write(long lba, byte[] bytes, WriteOptions options) {
        boolean random = lastLba != Long.MIN_VALUE && lba != lastLba + 1;
        if (random) {
            randomWrites++;
        }
        long latency = baseWriteMicros;
        if (isPastFillThreshold() && random && randomWrites % gcAfterRandomWrites == 0) {
            latency += gcStallMicros;
            gcStalls++;
        }
        if (options.forceUnitAccess() || options.flushAfter()) {
            latency += flushMicros;
            flushes++;
        }
        blocks.put(lba, ByteCopies.copyOf(bytes));
        lastLba = lba;
        writes++;
        simulatedMicros += latency;
        return IoResult.accepted(latency, latency > baseWriteMicros + flushMicros ? "ssd-gc-stall" : "ssd-write");
    }

    @Override
    public IoResult read(long lba, int length) {
        reads++;
        simulatedMicros += baseWriteMicros;
        return IoResult.accepted(baseWriteMicros, blocks.containsKey(lba) ? "ssd-read-hit" : "ssd-read-miss");
    }

    @Override
    public IoResult flush() {
        flushes++;
        simulatedMicros += flushMicros;
        return IoResult.accepted(flushMicros, "ssd-flush");
    }

    @Override
    public DeviceStats stats() {
        return new DeviceStats(reads, writes, flushes, 0, gcStalls, simulatedMicros);
    }

    private boolean isPastFillThreshold() {
        return ((double) blocks.size() / (double) capacityBlocks) >= fillThreshold;
    }
}
