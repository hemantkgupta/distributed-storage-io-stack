package com.hkg.distributedstorageiostack.page;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PageRecoveryTest {
    @Test
    void tornPageRecoversFromFullPageImage() {
        PageImage clean = new PageImage(7, 42, new byte[] {1, 1, 1, 1, 1, 1});
        PersistedPage torn = PersistedPage.from(clean).tornTail((byte) 9);

        PageRecovery.RecoveryResult result = new PageRecovery().recover(torn, Optional.of(clean), Optional.empty());

        assertThat(result.repaired()).isTrue();
        assertThat(result.source()).isEqualTo("full-page-image");
        assertThat(result.image().checksumMatches()).isTrue();
    }
}
