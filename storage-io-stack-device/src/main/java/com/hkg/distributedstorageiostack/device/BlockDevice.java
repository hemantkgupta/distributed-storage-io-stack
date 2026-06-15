package com.hkg.distributedstorageiostack.device;

/**
 * Minimal block-device contract used to teach write, flush, and zone semantics.
 */
public interface BlockDevice {
    IoResult write(long lba, byte[] bytes, WriteOptions options);

    IoResult read(long lba, int length);

    IoResult flush();

    DeviceStats stats();
}
