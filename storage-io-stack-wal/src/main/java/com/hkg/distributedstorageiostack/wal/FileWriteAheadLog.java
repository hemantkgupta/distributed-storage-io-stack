package com.hkg.distributedstorageiostack.wal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Minimal FileChannel WAL that separates append from force for group-commit labs.
 */
public final class FileWriteAheadLog implements AutoCloseable {
    private final Path path;
    private final FileChannel channel;
    private long nextLsn;
    private long forceCount;

    public FileWriteAheadLog(Path path) throws IOException {
        this.path = path;
        Files.createDirectories(path.getParent());
        this.channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public synchronized WalRecord append(byte[] payload, boolean force) throws IOException {
        WalRecord record = new WalRecord(nextLsn++, payload);
        channel.position(channel.size());
        channel.write(ByteBuffer.wrap(WalRecordCodec.encode(record)));
        if (force) {
            force();
        }
        return record;
    }

    public synchronized void force() throws IOException {
        channel.force(false);
        forceCount++;
    }

    public long forceCount() {
        return forceCount;
    }

    public List<WalRecord> replay() throws IOException {
        channel.force(false);
        byte[] bytes = Files.readAllBytes(path);
        return WalRecordCodec.decodeValidPrefix(bytes);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
