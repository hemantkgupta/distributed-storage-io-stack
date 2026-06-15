package com.hkg.distributedstorageiostack.page;

import java.util.Optional;

/**
 * Chooses a recovery source when checksum verification detects a torn page.
 */
public final class PageRecovery {
    public RecoveryResult recover(PersistedPage persisted, Optional<PageImage> fullPageImage, Optional<PageImage> doubleWriteCopy) {
        if (persisted.checksumMatches()) {
            return new RecoveryResult(false, "persisted-page", persisted.toCleanImage());
        }
        if (fullPageImage.isPresent()) {
            return new RecoveryResult(true, "full-page-image", fullPageImage.get());
        }
        if (doubleWriteCopy.isPresent()) {
            return new RecoveryResult(true, "doublewrite-copy", doubleWriteCopy.get());
        }
        throw new IllegalStateException("corrupted page has no recovery source");
    }

    public record RecoveryResult(boolean repaired, String source, PageImage image) {
    }
}
