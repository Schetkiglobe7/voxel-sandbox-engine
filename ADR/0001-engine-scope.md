# ADR 0001 – Engine Scope and Responsibilities

## Status

Accepted

## Context

The `voxel-sandbox-engine` aims to provide a long-lived, extensible, and
maintainable core for voxel-based applications.

Voxel engines tend to accumulate responsibilities over time, often mixing
engine-level concerns (world representation, chunk lifecycle, streaming,
performance guarantees) with game-specific logic (gameplay rules, UI,
progression systems).

This leads to:
- tight coupling between engine and game logic
- difficulty in evolving core systems
- unclear API stability guarantees
- reduced reusability across projects

A clear definition of **engine scope and responsibilities** is required to
prevent architectural drift and to establish a stable foundation for future
development.

## Decision

The `voxel-sandbox-engine` is defined as a **pure engine core**, responsible
only for **technical infrastructure**, not gameplay.

The engine is responsible for:

- voxel world representation
- chunk-based spatial partitioning
- chunk lifecycle management (load, generate, unload)
- deterministic world generation hooks
- world streaming and eviction mechanisms
- event-based observation of world changes
- API contracts and stability guarantees

The engine is explicitly **not responsible** for:

- rendering or graphics pipelines
- user interfaces
- player or entity logic
- gameplay rules or progression systems
- asset authoring or content pipelines
- persistence format decisions beyond core abstractions

All higher-level concerns must be implemented in **separate modules** or
applications built on top of the engine.

## Consequences

### Positive

- Clear separation between engine and game logic
- Improved long-term maintainability
- Easier testing and reasoning about core systems
- Ability to support multiple frontends (rendering, simulation, tooling)
- Stable API surface suitable for versioning

### Negative

- Additional integration effort for downstream projects
- Some features may feel incomplete without higher-level modules
- Rendering and persistence require separate implementation layers

## Alternatives Considered

### Monolithic Engine with Built-in Gameplay

A single engine including rendering, gameplay, and UI was considered.
This approach was rejected because it tightly couples concerns and makes
long-term evolution difficult.

### Engine with Opinionated Game Loop

An engine enforcing a specific game loop or entity model was also considered.
This was rejected in favor of flexibility and composability.

## Notes

This ADR defines the **non-negotiable boundary** of the engine.

Any future feature proposal that introduces gameplay semantics or rendering
responsibilities into the engine core must be rejected or handled in a
separate module.