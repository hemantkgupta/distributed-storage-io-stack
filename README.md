# Distributed Storage I/O Stack

A Java 17 multi-module reference implementation of the storage I/O contracts behind database storage engines: media latency shape, block-device flush semantics, WAL and group commit, torn-page recovery, cache ownership, LSM compaction debt, and replicated durability boundaries.

Companion code for the `storage-io-stack` and `storage-io-stack-full` blog posts in the CSE wiki.

## Status

Initial companion lab: 9 modules, deterministic tests, and a CLI that prints the lab surface. The implementation is intentionally a teaching artifact rather than a production database.

## Phase Plan

**Phase 1 - Foundation**
- CP1 - `storage-io-stack-common`: checksums, byte utilities, and shared records.
- CP2 - `storage-io-stack-device`: HDD/SSD/ZNS-flavored block-device simulators.

**Phase 2 - Durability**
- CP3 - `storage-io-stack-wal`: checksummed WAL records and group commit accounting.
- CP4 - `storage-io-stack-page`: page checksum, torn-page detection, and recovery source selection.

**Phase 3 - Engine Policy**
- CP5 - `storage-io-stack-cache`: recency versus semantic cache ownership.
- CP6 - `storage-io-stack-lsm`: compaction-debt and write-stall simulator.

**Phase 4 - Distributed + Ops**
- CP7 - `storage-io-stack-replication`: local fsync versus durable-quorum decision model.
- CP8 - `storage-io-stack-metrics`: small counters and histograms for lab output.
- CP9 - `storage-io-stack-lab`: executable summary CLI.

## Build

Requires JDK 17+.

```sh
./gradlew build --console=plain
./gradlew :storage-io-stack-wal:test --console=plain
```

## Architectural Anchors

- `fsync` is the durable local boundary: `wiki/concepts/fsync-durability.md`
- WAL-before-data is the recovery invariant: `wiki/concepts/write-ahead-log.md`
- Group commit changes the physical unit of durability: `wiki/concepts/group-commit.md`
- Torn writes require an explicit recovery source: `wiki/concepts/torn-write.md`
- Cache ownership is a design decision: `wiki/tradeoffs/buffered-vs-direct-io.md`
- LSM write throughput creates compaction debt: `wiki/concepts/compaction-debt.md`
- Replication changes but does not erase the durability boundary: `wiki/tradeoffs/local-fsync-vs-replicated-durability.md`

## License

Internal reference implementation; not for external distribution.
