# ADR 0005 – Engine Module Boundaries & API Stability

## Status

Accepted

## Context

With the release of `engine-v1.0.0`, the voxel-sandbox-engine reaches its first
functional and testable milestone.

The engine module now includes:

- a complete world model
- deterministic chunk lifecycle management
- explicit streaming and eviction mechanisms
- a well-defined event system
- a stable public API surface

As the project grows to include additional modules (rendering, persistence,
tooling, editor, integrations), it becomes critical to:

- clearly define **what the engine module is responsible for**
- explicitly declare **which APIs are considered stable**
- avoid accidental breaking changes
- allow other modules to evolve independently

Without clear boundaries, future development risks:
- API drift
- tight coupling between modules
- unintentional breaking changes
- unclear upgrade paths

---

## Decision

The project establishes **explicit module boundaries** and **API stability
guarantees** for the `engine` module.

---

## Engine Module Responsibilities

The `engine` module is responsible for:

- world representation and state management
- chunk lifecycle (generation, loading, unloading)
- deterministic world streaming
- eviction policy orchestration
- lifecycle event emission
- core domain abstractions (chunks, positions, voxel access)

The engine module:

- does NOT perform rendering
- does NOT manage graphics APIs
- does NOT persist data to storage
- does NOT implement gameplay logic
- does NOT assume a player or camera concept

The engine is a **headless, deterministic core**.

---

## Public API Definition

The following packages are considered part of the **public API**:

- `com.voxelsandbox.engine.world`
- `com.voxelsandbox.engine.world.chunk`
- `com.voxelsandbox.engine.world.type`
- `com.voxelsandbox.engine.world.event`
- `com.voxelsandbox.engine.world.streaming`
- `com.voxelsandbox.engine.world.eviction`

Classes and interfaces in these packages are:

- intended for external use
- covered by semantic versioning guarantees
- documented and tested

---

## Internal APIs

The following are considered **internal implementation details**:

- package-private classes
- classes explicitly documented as internal
- test utilities
- internal helper methods

Internal APIs:

- may change without notice
- are not covered by compatibility guarantees
- MUST NOT be relied upon by external modules

---

## API Stability Guarantees

Starting from `engine-v1.0.0`:

- Public APIs are **stable within the same major version**
- Breaking changes require a **major version bump**
- Additive, backward-compatible changes may occur in minor versions
- Bug fixes and internal refactors may occur in patch versions

These guarantees are documented in `API-STABILITY.md`.

---

## Versioning Strategy

The engine follows **Semantic Versioning**:

- **MAJOR** – breaking API changes
- **MINOR** – backward-compatible features
- **PATCH** – bug fixes and internal improvements

The `engine-v1.0.0` tag represents:

- the first frozen public API
- a reference baseline for future development
- a stable dependency point for other modules

---

## Consequences

### Positive

- Clear ownership of responsibilities
- Safe foundation for rendering, persistence, and tooling modules
- Predictable upgrade path
- Enables independent module evolution
- Reduces architectural entropy

### Negative

- Requires discipline when modifying public APIs
- Some future changes may require version bumps
- Additional documentation overhead

---

## Alternatives Considered

### No Explicit API Stability Policy

Rejected due to:

- high risk of breaking downstream modules
- unclear contract with contributors
- poor long-term maintainability

### Freezing Everything as Public API

Rejected due to:

- over-constraining internal refactors
- discouraging performance improvements
- unnecessary rigidity

---

## Notes

This ADR formally **freezes the engine public API** as of `engine-v1.0.0`.

Any breaking change to the engine module MUST:

- introduce a new ADR, and
- increment the major version.

This ADR serves as the architectural anchor for all future modules.