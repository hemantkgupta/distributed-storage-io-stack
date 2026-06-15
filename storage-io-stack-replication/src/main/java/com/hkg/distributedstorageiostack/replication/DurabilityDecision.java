package com.hkg.distributedstorageiostack.replication;

/**
 * Result of evaluating whether an acknowledgment mode survives a failure model.
 */
public record DurabilityDecision(boolean survives, String explanation) {
}
