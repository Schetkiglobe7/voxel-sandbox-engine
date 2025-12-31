<!-- Allow this file to not have a first line heading -->
<!-- markdownlint-disable-file MD041 no-emphasis-as-heading -->
<!-- markdownlint-disable-file MD033 -->

<div>

# 🧱 voxel-sandbox-engine

**A modern, data-driven voxel engine written in Java**

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
[![Contributor Covenant](https://img.shields.io/badge/contributor%20covenant-v1.4-ff69b4.svg)](CODE_OF_CONDUCT.md)
![Status](https://img.shields.io/badge/status-engine%20pre--release%201.0.1-blue)

</div>

---

## Overview

Voxel Sandbox Engine is an open-source project focused on building a **modern,
scalable, and maintainable voxel engine core**.

The project explores engine design choices that go beyond traditional voxel
engines, with particular emphasis on:

- clear separation between engine logic and game content
- deterministic and testable world behavior
- explicit chunk lifecycle and streaming semantics
- extensibility toward rendering, persistence, and tooling layers

This repository represents the **technical foundation of the engine**.
The core engine module is implemented and released as a **pre-release (v1.0.1)**,
while rendering, persistence, and tooling layers are planned as separate modules.

---

## Project Status

🧊 **Engine Core: Pre-release v1.0.1**

The engine core has reached its first stable architectural milestone.

Starting from `engine-v1.0.1`, the **public engine API is considered frozen**
for the `v1.x` line. Breaking changes will only be introduced in a future
major version.

### Implemented
- Chunk-based voxel world model
- Deterministic, seed-based world generation
- World bounds handling
- Chunk lifecycle management (generate / load / unload)
- Event-based world notifications with ordering guarantees
- Chunk streaming controllers (distance-based and fuzzy)
- Pluggable chunk eviction policies
- Deterministic and idempotent streaming behavior
- Extensive unit test coverage for world and streaming logic

### Not Implemented Yet
- Rendering system (OpenGL, Vulkan under evaluation)
- Persistence layer (database-backed world storage)
- Asset pipeline
- Gameplay systems
- Tooling and editor support

---

## Architecture Overview

The engine is structured around **clearly separated layers**, each with a
well-defined responsibility.

### Engine Core (Implemented)

The engine core manages:
- world state and chunk lifecycle
- deterministic world generation
- streaming and eviction policies
- event-based observation of world changes

This layer is **rendering-agnostic** and **persistence-agnostic** by design.

### Rendering Layer (Planned)

A dedicated rendering module will:
- consume read-only world state
- generate chunk-oriented meshes
- manage GPU resources independently from engine logic

The initial implementation will target OpenGL, with Vulkan evaluated as a
future alternative.

### Persistence & Data Layer (Planned)

A persistence module will:
- store chunk data beyond runtime
- support large or unbounded worlds
- provide spatial indexing and efficient loading

Database-backed storage (e.g. PostgreSQL with spatial extensions) is planned.

### Assets & Tooling (Planned)

Future modules will address:
- data-driven voxel and block definitions
- asset pipelines and content packs
- developer tools, debugging, and inspection utilities

---

## Architectural Decision Records (ADR)

Significant architectural decisions are documented as
**Architectural Decision Records (ADR)** in the [`ADR/`](ADR/) directory.

ADRs capture:
- design context
- decisions taken
- consequences and trade-offs

They serve as the authoritative source for architectural intent and evolution.

---

## Roadmap

The high-level project roadmap is maintained in [`ROADMAP.md`](ROADMAP.md).

Progress is tracked through:
- GitHub Issues and Pull Requests
- versioned releases
- Architectural Decision Records (ADR)

---

## Contributing

Community contributions are welcome.

Before contributing, please read:
- the Contributing Guide
- the Code of Conduct
- the Security Policy

For architectural or API-impacting changes, opening an issue or discussion
before implementation is strongly encouraged.

Engine APIs are considered stable starting from `engine-v1.0.1`.
Any breaking change requires a major version bump.

---

## License

This project is licensed under the Apache License, Version 2.0.

See the LICENSE file for details.