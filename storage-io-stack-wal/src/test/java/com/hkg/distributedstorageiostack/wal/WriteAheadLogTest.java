package com.hkg.distributedstorageiostack.wal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WriteAheadLogTest {
    @TempDir
    Path tempDir;

    @Test
    void replayStopsAtPartialTailRecord() throws IOException {
        Path walPath = tempDir.resolve("wal.log");
        byte[] valid = WalRecordCodec.encode(new WalRecord(0, new byte[] {1, 2, 3}));
        byte[] partial = Arrays.copyOf(WalRecordCodec.encode(new WalRecord(1, new byte[] {4, 5, 6})), 10);
        byte[] combined = new byte[valid.length + partial.length];
        System.arraycopy(valid, 0, combined, 0, valid.length);
        System.arraycopy(partial, 0, combined, valid.length, partial.length);
        Files.write(walPath, combined);

        List<WalRecord> records = WalRecordCodec.decodeValidPrefix(Files.readAllBytes(walPath));

        assertThat(records).hasSize(1);
        assertThat(records.get(0).lsn()).isEqualTo(0);
    }

    @Test
    void groupCommitUsesOneForceForManyRecords() throws IOException {
        try (FileWriteAheadLog wal = new FileWriteAheadLog(tempDir.resolve("wal.log"))) {
            GroupCommitCoordinator coordinator = new GroupCommitCoordinator(wal);

            GroupCommitCoordinator.BatchResult result = coordinator.commitBatch(List.of(
                    new byte[] {1},
                    new byte[] {2},
                    new byte[] {3}
            ));

            assertThat(result.records()).isEqualTo(3);
            assertThat(result.cumulativeForces()).isEqualTo(1);
            assertThat(wal.replay()).hasSize(3);
        }
    }
}
