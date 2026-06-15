package com.hkg.distributedstorageiostack.lab;

import com.hkg.distributedstorageiostack.lsm.LsmConfig;
import com.hkg.distributedstorageiostack.lsm.LsmDebtSimulator;
import com.hkg.distributedstorageiostack.replication.AckMode;
import com.hkg.distributedstorageiostack.replication.DurabilityModel;
import com.hkg.distributedstorageiostack.replication.FailureModel;

/**
 * CLI entry point that prints the teaching surface of the storage I/O lab.
 */
public final class StorageIoLab {
    private StorageIoLab() {
    }

    public static void main(String[] args) {
        LsmDebtSimulator simulator = new LsmDebtSimulator(new LsmConfig(1024, 4, 1024));
        simulator.ingest(6 * 1024L);
        boolean quorumSurvives = new DurabilityModel()
                .evaluate(AckMode.DURABLE_QUORUM, FailureModel.SINGLE_NODE_LOSS)
                .survives();
        System.out.println("storage-io-stack lab");
        System.out.println("l0Files=" + simulator.snapshot().l0Files());
        System.out.println("durableQuorumSurvivesSingleNodeLoss=" + quorumSurvives);
    }
}
