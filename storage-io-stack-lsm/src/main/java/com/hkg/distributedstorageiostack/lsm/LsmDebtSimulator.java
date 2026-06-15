package com.hkg.distributedstorageiostack.lsm;

/**
 * Deterministic LSM debt model: ingest flushes L0 files, compaction pays debt over time.
 */
public final class LsmDebtSimulator {
    private final LsmConfig config;
    private long activeMemtableBytes;
    private int l0Files;
    private long pendingCompactionBytes;

    public LsmDebtSimulator(LsmConfig config) {
        this.config = config;
    }

    public void ingest(long bytes) {
        activeMemtableBytes += bytes;
        while (activeMemtableBytes >= config.memtableBytes()) {
            activeMemtableBytes -= config.memtableBytes();
            l0Files++;
            pendingCompactionBytes += config.memtableBytes();
        }
    }

    public void compactOneTick() {
        long compacted = Math.min(config.compactionBytesPerTick(), pendingCompactionBytes);
        pendingCompactionBytes -= compacted;
        int filesCleared = (int) (compacted / config.memtableBytes());
        l0Files = Math.max(0, l0Files - filesCleared);
    }

    public LsmDebtSnapshot snapshot() {
        return new LsmDebtSnapshot(l0Files, pendingCompactionBytes, Math.max(1, l0Files), l0Files > config.maxL0Files());
    }
}
