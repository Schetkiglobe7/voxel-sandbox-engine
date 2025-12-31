# ADR 0002 – World and Chunk Model

## Status

Accepted

## Context

Voxel-based engines require an efficient spatial representation to support
large or infinite worlds while maintaining acceptable memory usage and
performance.

A naïve voxel-per-voxel world representation does not scale and makes
streaming, persistence, and parallelism difficult.

To address these concerns, the engine must adopt a structured spatial model
that:
- supports infinite horizontal expansion
- constrains vertical limits
- enables deterministic generation
- allows selective loading and unloading
- exposes a predictable lifecycle for observation and testing

## Decision

The engine adopts a **chunk-based world model**.

### World

The `World` represents a voxel space composed of discrete **chunks**.
It acts as the authoritative coordinator for:

- chunk generation
- chunk loading and unloading
- voxel access
- lifecycle event dispatch
- application of streaming and eviction policies

The world itself does not impose gameplay semantics.

### Chunk

A `Chunk` represents a fixed-size 3D block of voxels identified by a
`ChunkPosition` in chunk-space.

Chunks are:
- independently generated
- independently loaded or unloaded
- immutable in size
- mutable in voxel content

### Coordinate Spaces

The engine defines two coordinate spaces:

- **World voxel space**: absolute voxel coordinates `(x, y, z)`
- **Chunk space**: discrete chunk coordinates `(cx, cy, cz)`

A deterministic mapping exists between the two.

### World Bounds

The world is:
- **infinite in X and Z**
- **bounded in Y**

Vertical bounds are explicitly enforced:
- reads outside bounds return `AIR`
- writes outside bounds throw an exception

These rules are consistent across all world access paths.

### Chunk Lifecycle

Chunks follow a well-defined lifecycle:

1. **Absent** – chunk not present in world state
2. **Generated** – chunk created by the world generator
3. **Loaded** – chunk available for access
4. **Unloaded** – chunk removed from world state

Lifecycle transitions are observable via events.

## Consequences

### Positive

- Scales to large or infinite worlds
- Enables deterministic generation and streaming
- Supports lazy loading and eviction
- Clear lifecycle boundaries simplify testing
- World state remains explicit and inspectable

### Negative

- Requires careful handling of chunk boundaries
- Adds indirection for voxel access
- Chunk size becomes a design constraint

## Alternatives Considered

### Infinite Sparse Voxel Grid

A sparse voxel map without chunking was considered.
This approach was rejected due to poor cache locality and lack of
natural streaming boundaries.

### Octree-Based World

An octree representation was considered.
It was rejected due to increased complexity and reduced determinism
for chunk-based generation.

## Notes

This ADR establishes the **canonical spatial model** of the engine.

Any future changes to:
- chunk size
- coordinate mapping
- world bounds
- lifecycle semantics

must introduce a new ADR that supersedes this one.