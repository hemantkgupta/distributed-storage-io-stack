package com.hkg.distributedstorageiostack.device;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceSimulationTest {
    @Test
    void hddRandomWriteCostsMoreThanSequentialWrite() {
        SimulatedHddDevice device = new SimulatedHddDevice(8_000, 120, 8_000);

        IoResult first = device.write(0, new byte[] {1}, WriteOptions.none());
        IoResult sequential = device.write(1, new byte[] {2}, WriteOptions.none());
        IoResult random = device.write(100, new byte[] {3}, WriteOptions.none());

        assertThat(sequential.latency().micros()).isEqualTo(first.latency().micros());
        assertThat(random.latency().micros()).isGreaterThan(sequential.latency().micros());
    }

    @Test
    void ssdInjectsGcStallsWhenRandomWritesHitFilledDevice() {
        SimulatedSsdDevice device = new SimulatedSsdDevice(50, 200, 10_000, 2, 4, 0.50);

        device.write(0, new byte[] {1}, WriteOptions.none());
        device.write(1, new byte[] {1}, WriteOptions.none());
        device.write(10, new byte[] {1}, WriteOptions.none());
        device.write(20, new byte[] {1}, WriteOptions.none());

        assertThat(device.stats().gcStalls()).isEqualTo(1);
    }

    @Test
    void zonedDeviceRejectsOutOfOrderWrites() {
        ZonedBlockDevice device = new ZonedBlockDevice(4);

        assertThat(device.write(0, new byte[] {1}, WriteOptions.none()).accepted()).isTrue();
        IoResult rejected = device.write(2, new byte[] {1}, WriteOptions.none());

        assertThat(rejected.accepted()).isFalse();
        assertThat(device.stats().rejectedWrites()).isEqualTo(1);
    }
}
