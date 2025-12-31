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

This repository currently contains **only the core engine module**.

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