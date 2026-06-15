package com.hkg.distributedstorageiostack.lsm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LsmDebtSimulatorTest {
    @Test
    void writeStallEngagesWhenL0FilesExceedThreshold() {
        LsmDebtSimulator simulator = new LsmDebtSimulator(new LsmConfig(100, 3, 50));

        simulator.ingest(500);

        assertThat(simulator.snapshot().l0Files()).isEqualTo(5);
        assertThat(simulator.snapshot().writeStalled()).isTrue();
    }

    @Test
    void compactionPaysDownPendingDebt() {
        LsmDebtSimulator simulator = new LsmDebtSimulator(new LsmConfig(100, 10, 200));
        simulator.ingest(300);

        simulator.compactOneTick();

        assertThat(simulator.snapshot().pendingCompactionBytes()).isEqualTo(100);
        assertThat(simulator.snapshot().l0Files()).isEqualTo(1);
    }
}
