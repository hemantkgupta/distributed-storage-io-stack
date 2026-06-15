package com.hkg.distributedstorageiostack.lsm;

/**
 * Observable state of LSM compaction debt.
 */
public record LsmDebtSnapshot(int l0Files, long pendingCompactionBytes, int readAmplification, boolean writeStalled) {
}
