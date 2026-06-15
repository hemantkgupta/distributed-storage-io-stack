package com.hkg.distributedstorageiostack.replication;

/**
 * Acknowledgment modes used to compare memory replication with durable quorum.
 */
public enum AckMode {
    MEMORY_QUORUM,
    LOCAL_FSYNC,
    DURABLE_QUORUM
}
