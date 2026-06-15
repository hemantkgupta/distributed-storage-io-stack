package com.hkg.distributedstorageiostack.replication;

/**
 * Explicitly separates replicated-in-memory from replicated-and-durable.
 */
public final class DurabilityModel {
    public DurabilityDecision evaluate(AckMode mode, FailureModel failure) {
        if (mode == AckMode.DURABLE_QUORUM) {
            return new DurabilityDecision(true, "quorum persisted the entry");
        }
        if (mode == AckMode.LOCAL_FSYNC) {
            boolean survives = failure != FailureModel.SINGLE_NODE_POWER_LOSS || failure == FailureModel.PROCESS_CRASH;
            return new DurabilityDecision(survives, survives ? "local WAL is stable" : "only one local copy was durable");
        }
        boolean survives = failure == FailureModel.PROCESS_CRASH;
        return new DurabilityDecision(survives, survives ? "process died but peer memory survived" : "memory quorum lost power");
    }
}
