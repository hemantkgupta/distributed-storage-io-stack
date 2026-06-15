package com.hkg.distributedstorageiostack.replication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DurabilityModelTest {
    @Test
    void memoryQuorumDoesNotSurviveCorrelatedPowerLoss() {
        DurabilityDecision decision = new DurabilityModel().evaluate(AckMode.MEMORY_QUORUM, FailureModel.CORRELATED_POWER_LOSS);

        assertThat(decision.survives()).isFalse();
        assertThat(decision.explanation()).contains("memory");
    }

    @Test
    void durableQuorumSurvivesSingleNodeLoss() {
        DurabilityDecision decision = new DurabilityModel().evaluate(AckMode.DURABLE_QUORUM, FailureModel.SINGLE_NODE_LOSS);

        assertThat(decision.survives()).isTrue();
    }

    @Test
    void localFsyncDoesNotSurviveSingleNodeLoss() {
        DurabilityDecision decision = new DurabilityModel().evaluate(AckMode.LOCAL_FSYNC, FailureModel.SINGLE_NODE_LOSS);

        assertThat(decision.survives()).isFalse();
        assertThat(decision.explanation()).contains("one local copy");
    }
}
