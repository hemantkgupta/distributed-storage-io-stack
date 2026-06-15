package com.hkg.distributedstorageiostack.lsm;

/**
 * Knobs that control the compaction-debt simulator.
 */
public record LsmConfig(long memtableBytes, int maxL0Files, long compactionBytesPerTick) {
    public LsmConfig {
        if (memtableBytes <= 0 || maxL0Files <= 0 || compactionBytesPerTick <= 0) {
            throw new IllegalArgumentException("all config values must be positive");
        }
    }
}
