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
    void durableQuorumSurvivesSingleNodePowerLoss() {
        DurabilityDecision decision = new DurabilityModel().evaluate(AckMode.DURABLE_QUORUM, FailureModel.SINGLE_NODE_POWER_LOSS);

        assertThat(decision.survives()).isTrue();
    }
}
