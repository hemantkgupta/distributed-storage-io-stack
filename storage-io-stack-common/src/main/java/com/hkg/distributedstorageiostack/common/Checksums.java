package com.hkg.distributedstorageiostack.common;

import java.util.zip.CRC32C;

/**
 * CRC32C helper used by WAL records and page images to make corruption visible.
 */
public final class Checksums {
    private Checksums() {
    }

    public static int crc32c(byte[] bytes) {
        CRC32C crc = new CRC32C();
        crc.update(bytes, 0, bytes.length);
        return (int) crc.getValue();
    }
}
