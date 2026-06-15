package com.hkg.distributedstorageiostack.wal;

import com.hkg.distributedstorageiostack.common.ByteCopies;
import com.hkg.distributedstorageiostack.common.Checksums;

/**
 * Immutable WAL record with defensive payload copies and CRC32C validation.
 */
public final class WalRecord {
    private final long lsn;
    private final byte[] payload;
    private final int checksum;

    public WalRecord(long lsn, byte[] payload) {
        if (lsn < 0) {
            throw new IllegalArgumentException("lsn must be non-negative");
        }
        this.lsn = lsn;
        this.payload = ByteCopies.copyOf(payload);
        this.checksum = Checksums.crc32c(this.payload);
    }

    public long lsn() {
        return lsn;
    }

    public byte[] payload() {
        return ByteCopies.copyOf(payload);
    }

    public int checksum() {
        return checksum;
    }
}
