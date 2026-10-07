# ADR 0010: CPU World Image Pipeline

## Status
Accepted

## Context

The frame pipeline already provides camera rays and contiguous ray batches, but
only a dummy traversal stage existed. A portable reference image is needed before
GPU implementation so that world queries, intersections, and output can be tested
without a native window or graphics driver.

## Decision

- A scene input stage publishes camera, viewport, read-only voxel world, and a
  finite positive traversal distance through declared frame outputs.
- `CpuRayBatchTraversalStage` uses the existing DDA reference and solidity query.
  Batches must cover the complete ray buffer exactly once without overlaps.
- Results are immutable lists indexed by the original row-major ray position,
  regardless of batch execution order. Hits contain the intersection position,
  entry-face normal, and distance; misses contain infinite distance.
- `EngineVoxelWorldAdapter` uses `IWorldView` and the engine coordinate mapper.
  It never generates, loads, or mutates chunks. Traversal stops on unloaded chunks.
- A pixel output stage creates a fresh CPU target per frame and applies diagnostic
  face shading. PNG serialization belongs to the demo, outside render stages.
- The demo builds its world before rendering and runs with Java headless mode.
  The engine remains independent of rendering, image output, and native libraries.

## Consequences

The CPU pipeline is executable and testable on macOS, Windows, and Linux without
GLFW initialization. Each CI platform produces a preview artifact. Integration
tests check repeatability and PNG pixel round trips; these do not claim GPU parity
or byte-identical PNG compression across JDKs.

The reference renderer allocates per-ray data and is not a performance benchmark.
Shading is diagnostic and does not introduce block materials or an asset system.
Near clipping, interactive viewing, and native context lifecycle validation remain
future work. DDA uses the existing one-axis tie ordering (Z, then Y, then X).
