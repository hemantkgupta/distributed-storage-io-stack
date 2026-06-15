package com.hkg.distributedstorageiostack.page;

import com.hkg.distributedstorageiostack.common.ByteCopies;
import com.hkg.distributedstorageiostack.common.Checksums;

/**
 * Fixed-size page image with page LSN and checksum for torn-write detection.
 */
public final class PageImage {
    private final long pageId;
    private final long pageLsn;
    private final byte[] payload;
    private final int checksum;

    public PageImage(long pageId, long pageLsn, byte[] payload) {
        this.pageId = pageId;
        this.pageLsn = pageLsn;
        this.payload = ByteCopies.copyOf(payload);
        this.checksum = Checksums.crc32c(this.payload);
    }

    public long pageId() {
        return pageId;
    }

    public long pageLsn() {
        return pageLsn;
    }

    public byte[] payload() {
        return ByteCopies.copyOf(payload);
    }

    public int checksum() {
        return checksum;
    }

    public boolean checksumMatches() {
        return Checksums.crc32c(payload) == checksum;
    }

    public PageImage withCorruptedTail(byte fill) {
        byte[] copy = payload();
        for (int i = copy.length / 2; i < copy.length; i++) {
            copy[i] = fill;
        }
        return new PageImage(pageId, pageLsn, copy);
    }
}
