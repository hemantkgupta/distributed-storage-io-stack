package com.hkg.distributedstorageiostack.device;

/**
 * Observable counters from a simulated block device.
 */
public record DeviceStats(long reads, long writes, long flushes, long rejectedWrites, long gcStalls, long simulatedMicros) {
}
