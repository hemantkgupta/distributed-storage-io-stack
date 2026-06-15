package com.hkg.distributedstorageiostack.device;

import com.hkg.distributedstorageiostack.common.LatencySample;

import java.util.Optional;

/**
 * Result of a simulated device operation, including latency and rejection reason.
 */
public record IoResult(boolean accepted, LatencySample latency, Optional<String> rejectionReason) {
    public static IoResult accepted(long micros, String reason) {
        return new IoResult(true, new LatencySample(micros, reason), Optional.empty());
    }

    public static IoResult rejected(String reason) {
        return new IoResult(false, new LatencySample(0, "rejected"), Optional.of(reason));
    }
}
