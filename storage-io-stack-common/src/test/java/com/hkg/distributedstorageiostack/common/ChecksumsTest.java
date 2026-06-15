package com.hkg.distributedstorageiostack.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChecksumsTest {
    @Test
    void crcChangesWhenPayloadChanges() {
        int original = Checksums.crc32c(new byte[] {1, 2, 3});
        int changed = Checksums.crc32c(new byte[] {1, 2, 4});

        assertThat(changed).isNotEqualTo(original);
    }
}
