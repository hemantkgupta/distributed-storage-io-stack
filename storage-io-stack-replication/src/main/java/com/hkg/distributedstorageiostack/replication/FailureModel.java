package com.hkg.distributedstorageiostack.replication;

/**
 * Failure domains the storage stack may or may not survive.
 */
public enum FailureModel {
    PROCESS_CRASH,
    SINGLE_NODE_LOSS,
    CORRELATED_POWER_LOSS
}
