package com.hkg.distributedstorageiostack.device;

import com.hkg.distributedstorageiostack.common.ByteCopies;

import java.util.HashMap;
import java.util.Map;

/**
 * ZNS/SMR-style simulator: each zone must be written at its current append pointer.
 */
public final class ZonedBlockDevice implements BlockDevice {
    private final Map<Long, byte[]> blocks = new HashMap<>();
    private final Map<Long, Long> zonePointers = new HashMap<>();
    private final long zoneSizeBlocks;
    private long reads;
    private long writes;
    private long flushes;
    private long rejectedWrites;
    private long simulatedMicros;

    public ZonedBlockDevice(long zoneSizeBlocks) {
        if (zoneSizeBlocks <= 0) {
            throw new IllegalArgumentException("zone size must be positive");
        }
        this.zoneSizeBlocks = zoneSizeBlocks;
    }

    @Override
    public IoResult write(long lba, byte[] bytes, WriteOptions options) {
        long zone = lba / zoneSizeBlocks;
        long expected = zonePointers.getOrDefault(zone, zone * zoneSizeBlocks);
        if (lba != expected) {
            rejectedWrites++;
            return IoResult.rejected("zone append expected lba " + expected + " but got " + lba);
        }
        blocks.put(lba, ByteCopies.copyOf(bytes));
        zonePointers.put(zone, expected + 1);
        writes++;
        long latency = options.forceUnitAccess() || options.flushAfter() ? 140 : 40;
        if (options.forceUnitAccess() || options.flushAfter()) {
            flushes++;
        }
        simulatedMicros += latency;
        return IoResult.accepted(latency, "zoned-append");
    }

    @Override
    public IoResult read(long lba, int length) {
        reads++;
        simulatedMicros += 40;
        return IoResult.accepted(40, blocks.containsKey(lba) ? "zoned-read-hit" : "zoned-read-miss");
    }

    @Override
    public IoResult flush() {
        flushes++;
        simulatedMicros += 100;
        return IoResult.accepted(100, "zoned-flush");
    }

    @Override
    public DeviceStats stats() {
        return new DeviceStats(reads, writes, flushes, rejectedWrites, 0, simulatedMicros);
    }
}
