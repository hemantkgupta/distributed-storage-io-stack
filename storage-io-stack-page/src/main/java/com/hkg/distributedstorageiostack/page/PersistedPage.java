package com.hkg.distributedstorageiostack.page;

import com.hkg.distributedstorageiostack.common.ByteCopies;
import com.hkg.distributedstorageiostack.common.Checksums;

/**
 * Page bytes as found on disk, with stored checksum separated from payload.
 */
public final class PersistedPage {
    private final long pageId;
    private final long pageLsn;
    private final byte[] payload;
    private final int storedChecksum;

    public PersistedPage(long pageId, long pageLsn, byte[] payload, int storedChecksum) {
        this.pageId = pageId;
        this.pageLsn = pageLsn;
        this.payload = ByteCopies.copyOf(payload);
        this.storedChecksum = storedChecksum;
    }

    public static PersistedPage from(PageImage image) {
        return new PersistedPage(image.pageId(), image.pageLsn(), image.payload(), image.checksum());
    }

    public PersistedPage tornTail(byte fill) {
        byte[] copy = ByteCopies.copyOf(payload);
        for (int i = copy.length / 2; i < copy.length; i++) {
            copy[i] = fill;
        }
        return new PersistedPage(pageId, pageLsn, copy, storedChecksum);
    }

    public boolean checksumMatches() {
        return Checksums.crc32c(payload) == storedChecksum;
    }

    public PageImage toCleanImage() {
        if (!checksumMatches()) {
            throw new IllegalStateException("cannot convert corrupted persisted page");
        }
        return new PageImage(pageId, pageLsn, payload);
    }
}
