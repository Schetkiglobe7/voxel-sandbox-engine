# API Stability Policy

This document defines the **API stability guarantees** for the
`voxel-sandbox-engine` project, with a specific focus on the `engine` module.

This policy is effective starting from **engine-v1.0.0**.

---

## Scope

This policy applies to the **engine module only**.

The engine module is a **headless, deterministic core** responsible for:

- world state management
- chunk lifecycle and streaming
- eviction policies
- lifecycle event emission
- core domain abstractions

Rendering, persistence, tooling, and gameplay systems are explicitly
out of scope for this policy.

---

## Public API

The following packages constitute the **public API** of the engine:

- `com.voxelsandbox.engine.world`
- `com.voxelsandbox.engine.world.chunk`
- `com.voxelsandbox.engine.world.type`
- `com.voxelsandbox.engine.world.event`
- `com.voxelsandbox.engine.world.streaming`
- `com.voxelsandbox.engine.world.eviction`

Classes and interfaces in these packages are:

- intended for external use
- documented and tested
- covered by semantic versioning guarantees

---

## Internal API

All other code is considered **internal**.

Internal APIs:

- are not guaranteed to remain stable
- may change without notice
- MUST NOT be relied upon by external modules

Internal code includes, but is not limited to:

- package-private classes
- internal helpers
- test utilities
- implementation details not explicitly documented as public

---

## Stability Guarantees

Starting from `engine-v1.0.0`:

- **MAJOR versions** may introduce breaking API changes
- **MINOR versions** may introduce backward-compatible additions
- **PATCH versions** are limited to bug fixes and internal refactors

Breaking changes include (but are not limited to):

- removal or renaming of public classes or methods
- incompatible method signature changes
- changes to documented behavioral guarantees
- changes to lifecycle event semantics or ordering

---

## Behavioral Stability

API stability includes **behavioral guarantees**, not just method signatures.

In particular, the following are considered stable:

- chunk lifecycle semantics (generate / load / unload)
- event emission guarantees and ordering
- deterministic streaming behavior
- eviction policy contracts

Any change to these behaviors requires:

- a new Architectural Decision Record (ADR)
- a major version increment

---

## Experimental and Future APIs

New APIs may be introduced as **experimental**.

Experimental APIs:

- will be clearly documented as such
- may change or be removed in minor versions
- do not carry long-term stability guarantees

Once an API is declared stable, it is subject to this policy.

---

## Relationship with ADRs

Architectural Decision Records (ADRs) are **authoritative** with respect to
engine behavior and guarantees.

If a conflict arises between code and an accepted ADR:

- the ADR takes precedence
- the change must be documented via a new ADR

See `ADR/0005-engine-module-boundaries-and-api-stability.md`.

---

## Summary

- The engine API is **stable starting from v1.0.0**
- Public APIs follow **Semantic Versioning**
- Internal APIs may change freely
- Behavioral guarantees are part of the API contract
- Breaking changes require explicit documentation and versioning

This policy exists to ensure long-term maintainability, trust, and
predictability for contributors and downstream modules.