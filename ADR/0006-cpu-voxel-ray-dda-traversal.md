# ADR-0006: CPU Voxel Ray Traversal using DDA

## Status
Accepted

## Context
The engine requires a deterministic, CPU-side reference implementation
for voxel ray traversal in order to support:

- voxel picking
- ray casting
- hit detection
- correctness validation
- future GPU parity

Traversal must work independently of rendering backends,
support infinite worlds, and avoid runtime allocations
inside the traversal loop.

## Decision
Voxel ray traversal is implemented using a classic
Digital Differential Analyzer (DDA) algorithm.

The solution is structured into the following components:

- `Ray3f`, `Vec3f`, `Mat4f`: immutable math primitives
- `VoxelRayTraversalState`: mutable DDA state (voxel, step, tMax, tDelta)
- `VoxelRayInitializer`: converts a continuous ray into DDA parameters
- `VoxelRayStepper`: advances the traversal by exactly one voxel
- `IVoxelRayTraversal`: traversal contract
- `CpuVoxelRayTraversal`: CPU reference implementation

Traversal logic is decoupled from world representation via
`IVoxelWorldView`, enabling infinite or chunk-based worlds.

Voxel solidity is evaluated through a `VoxelHitPredicate`.

Face detection is performed by tracking the axis of the
last DDA step and deriving the surface normal accordingly.

## Consequences

### Positive
- Deterministic voxel visitation order
- Allocation-free traversal loop
- Clear separation between math, traversal and world logic
- Correct face normal detection without geometry tests
- Suitable as reference for GPU shader implementation

### Negative
- CPU traversal is not optimized for large ray batches
- Floating-point precision requires careful handling

## Notes
The implementation follows the Amanatides & Woo DDA algorithm
and matches traversal behavior used in established voxel engines.

This implementation is considered the canonical reference
for all future ray-based voxel interactions in the engine.