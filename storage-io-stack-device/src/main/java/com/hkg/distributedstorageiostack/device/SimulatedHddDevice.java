package com.hkg.distributedstorageiostack.device;

import com.hkg.distributedstorageiostack.common.ByteCopies;

import java.util.HashMap;
import java.util.Map;

/**
 * HDD-shaped simulator: sequential writes are cheap, seeks dominate random I/O.
 */
public final class SimulatedHddDevice implements BlockDevice {
    private final Map<Long, byte[]> blocks = new HashMap<>();
    private final long seekMicros;
    private final long sequentialMicros;
    private final long flushMicros;
    private long lastLba = Long.MIN_VALUE;
    private long reads;
    private long writes;
    private long flushes;
    private long simulatedMicros;

    public SimulatedHddDevice(long seekMicros, long sequentialMicros, long flushMicros) {
        this.seekMicros = seekMicros;
        this.sequentialMicros = sequentialMicros;
        this.flushMicros = flushMicros;
    }

    @Override
    public IoResult write(long lba, byte[] bytes, WriteOptions options) {
        long latency = (lastLba == Long.MIN_VALUE || lba == lastLba + 1) ? sequentialMicros : seekMicros;
        if (options.flushBefore()) {
            latency += flushMicros;
        }
        if (options.forceUnitAccess() || options.flushAfter()) {
            latency += flushMicros;
            flushes++;
        }
        blocks.put(lba, ByteCopies.copyOf(bytes));
        lastLba = lba;
        writes++;
        simulatedMicros += latency;
        return IoResult.accepted(latency, lba == lastLba ? "hdd-write" : "hdd-seek");
    }

    @Override
    public IoResult read(long lba, int length) {
        reads++;
        simulatedMicros += seekMicros;
        return IoResult.accepted(seekMicros, blocks.containsKey(lba) ? "hdd-read-hit" : "hdd-read-miss");
    }

    @Override
    public IoResult flush() {
        flushes++;
        simulatedMicros += flushMicros;
        return IoResult.accepted(flushMicros, "hdd-flush");
    }

    @Override
    public DeviceStats stats() {
        return new DeviceStats(reads, writes, flushes, 0, 0, simulatedMicros);
    }
}
