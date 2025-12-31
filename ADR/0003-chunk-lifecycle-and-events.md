# ADR 0003 – Chunk Lifecycle and Events

## Status

Accepted

## Context

Chunk generation, loading, and unloading are fundamental operations in a
streamed voxel world.

Multiple subsystems (rendering, caching, persistence, debugging, telemetry)
need to observe chunk lifecycle changes without mutating world state.

Without explicit guarantees, lifecycle events can:
- be emitted multiple times
- occur in ambiguous order
- cause accidental world mutation
- make testing unreliable

A clear, deterministic event model is therefore required.

## Decision

The engine exposes **explicit chunk lifecycle events** via a listener interface.

### Event Interface

Lifecycle events are exposed through `IWorldEventListener`.

The interface provides default (no-op) methods to allow selective
implementation:

- `onChunkGenerated`
- `onChunkLoaded`
- `onChunkUnloaded`

### Event Semantics

#### Chunk Generated

- Emitted **exactly once per chunk position**
- Occurs immediately after a chunk is created by the generator
- Emitted **before** the chunk is considered loaded

#### Chunk Loaded

- Emitted every time a chunk becomes available
- Emitted for both:
    - newly generated chunks
    - already present chunks accessed again
- May be emitted multiple times for the same chunk

#### Chunk Unloaded

- Emitted **exactly once per successful unload**
- Only emitted if a chunk was actually present
- Emitted after removal from world state

### Ordering Guarantees

For a newly generated chunk:

onChunkGenerated → onChunkLoaded

For an already present chunk:

onChunkLoaded

For unloading:

onChunkUnloaded

No other ordering guarantees are provided.

### Threading Model

- Event listeners are invoked **synchronously**
- No parallelism is guaranteed
- Listeners must be non-blocking
- Listeners MUST NOT mutate world state

This is a deliberate design choice for engine v1.x.

Parallel dispatch may be introduced in the future via a new ADR.

## Consequences

### Positive

- Deterministic event behavior
- Testable lifecycle guarantees
- Decouples engine core from observers
- Supports future rendering and persistence layers

### Negative

- Requires discipline in listener implementations
- Event ordering must be preserved in future refactors

## Alternatives Considered

### Implicit Events via State Polling

Rejected due to:
- inefficiency
- lack of determinism
- poor testability

### Asynchronous Event Dispatch

Deferred to a future ADR to avoid premature concurrency complexity.

## Notes

This ADR defines **observable engine behavior**.

Any change to:
- event ordering
- emission frequency
- threading guarantees

MUST be documented via a superseding ADR.