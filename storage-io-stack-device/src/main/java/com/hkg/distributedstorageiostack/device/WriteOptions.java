package com.hkg.distributedstorageiostack.device;

/**
 * Host-visible write flags that map to flush/FUA style storage contracts.
 */
public record WriteOptions(boolean forceUnitAccess, boolean flushBefore, boolean flushAfter) {
    public static WriteOptions none() {
        return new WriteOptions(false, false, false);
    }

    public static WriteOptions durable() {
        return new WriteOptions(true, false, true);
    }
}
