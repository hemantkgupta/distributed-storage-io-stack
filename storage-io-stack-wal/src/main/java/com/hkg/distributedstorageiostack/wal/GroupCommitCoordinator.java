package com.hkg.distributedstorageiostack.wal;

import java.io.IOException;
import java.util.List;

/**
 * Deterministic group-commit coordinator: many logical records, one physical force.
 */
public final class GroupCommitCoordinator {
    private final FileWriteAheadLog wal;

    public GroupCommitCoordinator(FileWriteAheadLog wal) {
        this.wal = wal;
    }

    public BatchResult commitBatch(List<byte[]> payloads) throws IOException {
        if (payloads.isEmpty()) {
            return new BatchResult(0, wal.forceCount());
        }
        for (byte[] payload : payloads) {
            wal.append(payload, false);
        }
        wal.force();
        return new BatchResult(payloads.size(), wal.forceCount());
    }

    public record BatchResult(int records, long cumulativeForces) {
    }
}
