package com.hkg.distributedstorageiostack.wal;

import com.hkg.distributedstorageiostack.common.Checksums;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Binary WAL codec. Invalid or partial tails are ignored during replay.
 */
public final class WalRecordCodec {
    private static final int MAGIC = 0x57414c31;
    private static final int HEADER_BYTES = Integer.BYTES + Long.BYTES + Integer.BYTES + Integer.BYTES;

    private WalRecordCodec() {
    }

    public static byte[] encode(WalRecord record) {
        byte[] payload = record.payload();
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_BYTES + payload.length);
        buffer.putInt(MAGIC);
        buffer.putLong(record.lsn());
        buffer.putInt(payload.length);
        buffer.putInt(record.checksum());
        buffer.put(payload);
        return buffer.array();
    }

    public static List<WalRecord> decodeValidPrefix(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        List<WalRecord> records = new ArrayList<>();
        while (buffer.remaining() >= HEADER_BYTES) {
            int position = buffer.position();
            int magic = buffer.getInt();
            long lsn = buffer.getLong();
            int length = buffer.getInt();
            int checksum = buffer.getInt();
            if (magic != MAGIC || length < 0 || buffer.remaining() < length) {
                buffer.position(position);
                break;
            }
            byte[] payload = new byte[length];
            buffer.get(payload);
            if (Checksums.crc32c(payload) != checksum) {
                buffer.position(position);
                break;
            }
            records.add(new WalRecord(lsn, payload));
        }
        return records;
    }
}
