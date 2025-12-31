# ADR 0004 – World Streaming & Eviction

## Status

Accepted

## Context

A voxel world with potentially unbounded dimensions cannot keep all chunks
loaded in memory at all times.

To support scalability, performance, and future rendering and persistence
layers, the engine must:

- load chunks dynamically based on relevance
- unload chunks that are no longer needed
- provide deterministic and testable behavior
- decouple *selection* of chunks from *unloading mechanics*

Early design choices must avoid:

- implicit or hidden chunk loading
- hard-coded eviction heuristics
- tight coupling between world logic and player/camera logic
- non-deterministic unload behavior

A clear and extensible **world streaming model** is therefore required.

---

## Decision

The engine adopts an explicit **World Streaming & Eviction architecture**
based on three distinct concepts:

1. **Focus-based streaming**
2. **Pluggable eviction policies**
3. **Explicit unload operations with lifecycle events**

---

## Streaming Model

### Focus Position

World streaming is driven by a **focus chunk position**, typically representing:

- player position
- camera position
- AI or simulation interest point

The engine itself does **not** assume what the focus represents.

---

### Streaming Controllers

Streaming behavior is orchestrated by implementations of:

`IChunkStreamingController`

A streaming controller is responsible for:

- deciding which chunks must be loaded
- invoking world loading operations
- applying an eviction policy

The controller:

- does NOT contain eviction logic
- does NOT decide which chunks to unload directly
- delegates unloading to the world via eviction policies

---

### Distance-Based Streaming

The engine provides a reference implementation:

`DistanceBasedChunkStreamingController`

Characteristics:

- eagerly loads a cubic region of chunks around the focus
- region size defined by a configurable `loadRadius`
- deterministic and idempotent behavior
- suitable for early engine versions and testing

---

## Eviction Model

### Eviction Policies

Chunk eviction decisions are delegated to implementations of:

`IChunkEvictionPolicy`

An eviction policy is responsible for:

- selecting chunks that are no longer relevant
- optionally triggering unloading via the world API

Eviction policies MUST:

- never generate or load chunks
- never mutate world state except via `World.unloadChunk`
- be deterministic for the same inputs

---

### Separation of Concerns

Eviction is explicitly split into two phases:

1. **Selection**
    - implemented via `selectEvictionCandidates`
    - pure function
    - no side effects

2. **Application**
    - performed by `World.applyEvictionPolicy`
    - responsible for:
        - unloading chunks
        - emitting lifecycle events

This separation ensures:

- testability
- reuse of eviction logic
- clear ownership of world mutations

---

### Supported Eviction Strategies

The engine currently provides:

#### Distance-Based Eviction

`DistanceBasedChunkEvictionPolicy`

- Uses Euclidean distance in chunk space
- Hard threshold-based selection
- Simple and predictable behavior

#### Fuzzy Distance-Based Eviction

`FuzzyDistanceChunkEvictionPolicy`

- Uses a smooth hyperbolic tangent (tanh) distance function
- Produces gradual eviction behavior
- Reduces sudden unload spikes during focus movement
- Suitable for real-time world streaming

---

## Lifecycle Guarantees

- Streaming controllers may trigger:
    - chunk generation
    - chunk loading
    - chunk unloading

- All unload operations:
    - go through `World.unloadChunk`
    - emit `onChunkUnloaded` **exactly once per chunk**

- Streaming controllers are:
    - deterministic
    - idempotent for the same focus position

---

## Consequences

### Positive

- Clear separation of responsibilities
- Deterministic and testable streaming behavior
- Extensible eviction strategies
- Supports future persistence and rendering layers
- No implicit world mutations

### Negative

- Slightly larger API surface
- Requires discipline when implementing custom policies
- Streaming logic must be explicitly invoked by the application

---

## Alternatives Considered

### Implicit Streaming Inside World Accessors

Rejected due to:

- hidden side effects
- poor debuggability
- difficulty testing world behavior
- tight coupling between read operations and mutation

### Single Monolithic Streaming System

Rejected due to:

- lack of extensibility
- inability to swap eviction strategies
- premature optimization

---

## Notes

This ADR defines **core engine behavior**.

Any change to:

- streaming triggers
- eviction semantics
- unloading guarantees

MUST be documented via a superseding ADR.

Future ADRs may introduce:

- memory-based eviction
- time-based eviction
- persistence-aware streaming
- multi-focus streaming