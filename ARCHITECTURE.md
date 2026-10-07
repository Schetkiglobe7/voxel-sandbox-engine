# Architecture Overview

This document describes the high-level architecture of the **Voxel Sandbox Engine**,
focusing on the design principles, core subsystems, and interaction patterns
implemented in the `engine` module.

The goal of this architecture is to provide a **deterministic, extensible, and
data-driven foundation** for voxel-based simulations and games, while keeping
rendering, persistence, and gameplay concerns strictly decoupled.

---

## Architectural Principles

The engine is designed around the following core principles:

### Determinism
Given the same seed, inputs, and sequence of updates, the engine must produce
the same world state. This applies to chunk generation, loading, unloading,
and streaming behavior.

### Idempotency
Most public engine operations (e.g. streaming updates) are designed to be
idempotent: invoking them multiple times with the same parameters does not
produce unintended side effects.

### Separation of Concerns
The engine strictly separates:
- world state management
- chunk generation
- chunk streaming and eviction
- rendering (not part of this module)
- persistence (not part of this module)

### Data-Driven Design
Core systems are built around strategies and policies (e.g. generators,
eviction policies, streaming controllers) that can be replaced or extended
without modifying engine internals.

---

## Module Scope

This repository contains the stable `engine` module and an experimental
`render-system` module. The latter depends on the engine through read-only
adapters; the engine does not depend on rendering or native libraries.

The render system includes frame contract validation (ADRs 0007–0009), CPU
camera and ray stages, voxel DDA traversal, and an in-memory ARGB render target.
Render targets expose dimensions, frame boundaries, and pixel output independently
of graphics APIs. CPU targets clear to transparent at frame start, reject writes
outside a frame, and enforce pixel bounds. OpenGL rendering remains a placeholder; the CPU preview is executable.

Java 17 and the checked-in Gradle Wrapper provide the portable build. The engine
application generates POSIX and Windows launch scripts. Native dependencies in
`render-system` follow the host OS and JVM architecture (x86-64 or ARM64).

### Included
- World and chunk lifecycle management
- Deterministic chunk generation
- Chunk streaming and eviction
- Event-driven world notifications
- Explicit API stability guarantees

### Explicitly Excluded
- Rendering (OpenGL, Vulkan, etc.)
- Physics and gameplay systems
- Networking and multiplayer
- Persistence and databases
- Asset pipelines and tooling

These concerns will be addressed in separate modules.

---

## Core Subsystems

### World

The `World` class represents the central coordination point of the engine.

Responsibilities:
- Owns the world seed
- Delegates chunk generation
- Manages chunk lifecycle (load, unload)
- Emits world events
- Applies eviction policies

The `World` does **not**:
- Render chunks
- Persist data
- Make gameplay decisions

---

### WorldState

`WorldState` is an internal, mutable component responsible for storing
loaded chunks.

Key properties:
- Not exposed publicly
- No side effects beyond state mutation
- No event emission
- No generation logic

This separation ensures that:
- lifecycle logic remains explicit
- event emission is always controlled by `World`

---

### Chunk Model

Chunks are identified by immutable `ChunkPosition` coordinates
in chunk-space.

Chunk data:
- is generated deterministically
- is mutable once loaded
- exists only while loaded in memory

Voxel access is performed through chunk-local coordinates derived
from world coordinates.

---

### Chunk Generation

Chunk generation is delegated to implementations of `IWorldGenerator`.

Properties:
- deterministic
- stateless
- pure with respect to input parameters

The engine guarantees that:
- a chunk is generated at most once per position
- generation always precedes loading events

---

### World Events

The engine exposes lifecycle events through `IWorldEventListener`.

Supported events:
- `onChunkGenerated`
- `onChunkLoaded`
- `onChunkUnloaded`

Event guarantees:
- generation is emitted exactly once per chunk
- load events may be emitted multiple times
- unload events are emitted exactly once per removal
- no events are emitted during read-only operations

Listeners must be:
- side-effect free with respect to world state
- thread-safe if used in concurrent contexts

---

### Chunk Streaming

Chunk streaming is orchestrated by implementations of
`IChunkStreamingController`.

Responsibilities:
- decide which chunks must be loaded
- delegate eviction to a policy
- coordinate world updates around a focus position

Streaming controllers are:
- deterministic
- idempotent
- stateless with respect to world contents

---

### Chunk Eviction

Eviction logic is defined through `IChunkEvictionPolicy`.

Responsibilities:
- select eviction candidates
- optionally trigger unloads via the `World`

Two strategies are currently provided:
- distance-based eviction (hard threshold)
- fuzzy distance-based eviction (tanh-based)

Eviction policies:
- must not generate or load chunks
- must not mutate world state except via unloading

---

## Execution Flow (High Level)

A typical update cycle follows this pattern:

1. A streaming controller is invoked with a focus position
2. Required chunks around the focus are loaded
3. An eviction policy selects distant chunks
4. Selected chunks are unloaded
5. Corresponding world events are emitted

This flow is explicit and fully testable.

---

## Testing Strategy

The engine follows a strict testing philosophy:

- unit tests for world invariants
- lifecycle event correctness tests
- streaming and eviction behavior tests
- idempotency and determinism checks

All tests are deterministic and do not rely on timing or concurrency.

---

## Evolution and Stability

Public APIs are governed by the rules defined in `API-STABILITY.md`.

Architectural decisions are documented as ADRs in the `ADR/` directory.

Breaking changes are explicitly versioned and documented.

---

## Summary

The Voxel Sandbox Engine provides a minimal but solid core focused on:
- correctness
- extensibility
- architectural clarity

It is intended to serve as a foundation upon which rendering, persistence,
and gameplay systems can be built independently.

## CPU Ray Traversal Integration

`CpuRayBatchTraversalStage` reads origins, directions, batches, a read-only world,
and a finite positive maximum distance exclusively from the frame. It validates
exact batch coverage before traversal and publishes an immutable result list in
original ray order. Hit positions are world-space intersection points; misses
carry infinite distance. `EngineVoxelWorldAdapter` queries `IWorldView` without
loading chunks and handles negative coordinates with floor division.

DDA visits report voxel entry distances, stop at the first unloaded region, and
include hits exactly at the distance limit. Starting inside solid returns distance
zero and a zero normal. Directions must be finite unit vectors.


## CPU World Preview

The executable `CpuRenderDemo` assembles a world before rendering, then executes
scene input, camera matrices, ray generation, batching, DDA traversal, and pixel
output in a strict `CpuRenderFrame`. Rays and results use row-major pixel order.
Batches describe contiguous slices, not spatial tiles. `CpuPixelOutputStage`
allocates a fresh target, shades voxel faces diagnostically, and fills misses with
sky color. PNG serialization runs outside the stages using Java ImageIO in
headless mode. See ADR 0010.

The demo does not initialize GLFW. It produces an image rather than an interactive
window; native window presentation is implemented separately; GPU voxel rendering remains pending.


## Native Window Presentation

`OpenGLWindow` owns a GLFW session, window, and thread-local capabilities on one
thread. Hidden-context and legacy bootstrap helpers delegate to it. It cleans up
partial initialization and rejects concurrent native owners. CPU stages and the
engine remain independent of this lifecycle.

`OpenGLPreviewDemo` renders one CPU image, then `CpuImagePresenter` uploads it as
RGBA and presents it through an OpenGL 3.3 core shader. The event loop uses physical
framebuffer dimensions for high-DPI sizing and letterboxing; Escape and window
close end the loop. Native resources close before the context is destroyed.

`nativeSmoke` separately tests context restart, competing-owner rejection, resize, and real pixel readback. Mock driver
tests exercise cleanup failures without a display. See ADR 0011. Interactive camera movement and CPU frame refresh build on this baseline; no GPU
voxel traversal is introduced.


## Interactive Camera and Cached Frames

The preview owns a `PreviewCameraController` in its demo layer. A GLFW adapter
turns held keys and cursor drags into immutable `CameraInput` snapshots; controller
math depends only on those inputs and elapsed time. Pitch, combined movement, and
stalled timesteps are bounded. Immutable camera snapshots feed the existing stages.

`CpuWorldRenderer` retains one read-only world. `InteractiveCpuPreview` caches its
pixel target until the pose changes; a render uses a fresh strict frame rather than
retaining intermediate stage data. Presentation resize does not invalidate the
fixed 320 × 200 image. Texture updates reuse GPU storage and one direct staging
buffer, which closes before the native context. Unfocused/minimized windows do not
advance the camera; releasing the drag or losing focus resets pointer history.

No world generation, collision, or chunk streaming occurs in the input or rendering
loop. See ADR 0012 for controls, timing, and remaining performance constraints.
