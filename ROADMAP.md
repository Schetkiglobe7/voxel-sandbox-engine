# Roadmap

This document outlines the planned evolution of the **Voxel Sandbox Engine** project.

The roadmap is intentionally high-level and focuses on **architectural milestones**
rather than detailed task breakdowns. Fine-grained progress is tracked via
GitHub Issues, Pull Requests, and **Architectural Decision Records (ADR)**.

---

## Current Status

✅ **Engine Core v1.0.0 (Pre-release)**

The engine core is **feature-complete, tested, and API-stable** for the `v1.x` line.

This pre-release establishes a **frozen architectural foundation** on which all
future modules (rendering, persistence, tooling) will be built.

No further breaking changes to the engine core API are expected before `v2.0.0`.

---

## Phase 1 — Engine Foundation (Completed)

**Status:** ✅ Completed  
**Version:** `engine-v1.0.0`

### Delivered
- Chunk-based voxel world model
- Deterministic world generation (seed-based)
- Chunk lifecycle management (generate / load / unload)
- World event system with strict ordering guarantees
- Chunk streaming infrastructure
- Distance-based eviction policies
- Fuzzy (tanh-based) eviction policies
- Deterministic and idempotent streaming controllers
- Extensive unit test coverage
- API stability and versioning strategy
- Architectural Decision Records (ADR) system

This phase focused exclusively on **correctness, architectural clarity, and
extensibility**.

Rendering, persistence, and tooling concerns were intentionally excluded.

---

## Phase 2 — Rendering Layer

**Status:** 🚧 In progress

### Goals
- Introduce a rendering module fully decoupled from engine logic
- Preserve engine determinism and testability
- Enable multiple rendering backends over time

### Delivered Foundation
- Read-only adapters between engine and rendering
- Camera, CPU ray generation, batching, and voxel traversal
- Frame-driven pipelines with strict stage contracts (ADRs 0007–0009)
- CPU pixel target and restored rendering compilation
- Gradle Wrapper and headless application launchers
- OS/architecture-specific LWJGL natives and three-platform CI

### Completed CPU Traversal
- Concrete CPU ray batch traversal with order-independent batches
- Correct DDA entry distances, starting-inside-solid behavior, and regression tests

### Completed CPU World Preview
- Strict scene → camera → rays → batches → traversal → pixel output pipeline
- Headless executable demo and PNG export with diagnostic face shading
- Repeatability and PNG pixel round-trip integration tests
- Three-platform CI runs the demo and uploads preview artifacts

### Verification (2026-10-07)
- macOS: full build, 57 passing tests, both demos, and installed CPU launcher verified
- CPU PNG inspected; repeatability and lossless export covered by integration tests
- Windows/Linux: CI configured; execution results pending branch publication

### Next Increment
- Verify native context lifecycle before implementing GPU rendering

### Planned Topics
- OpenGL-based renderer (initial implementation)
- Evaluation of Vulkan as a future alternative
- Chunk-oriented mesh generation
- GPU-friendly data structures
- Visibility and frustum culling
- Render pipeline abstraction

> Rendering will live in a dedicated module and will not affect the engine core API.

---

## Phase 3 — Persistence & World Storage

**Status:** ⏳ Planned

### Goals
- Persist world data beyond runtime
- Support large and potentially unbounded worlds
- Enable offline processing and tooling

### Planned Topics
- Database-backed chunk storage
- Spatial indexing (PostGIS or equivalent)
- Chunk serialization formats
- Lazy loading from persistent storage
- Separation between in-memory and persistent representations

---

## Phase 4 — Assets & Data-Driven Content

**Status:** ⏳ Planned

### Goals
- Move from hard-coded content to data-driven definitions
- Support multiple content packs
- Enable modding and experimentation

### Planned Topics
- Asset pipeline (textures, block definitions, materials)
- Data-driven voxel and block definitions
- Versioned content packs
- Runtime asset reloading

---

## Phase 5 — Tooling & Developer Experience

**Status:** ⏳ Planned

### Goals
- Improve developer productivity
- Enable inspection, debugging, and visualization

### Planned Topics
- World inspection tools
- Debug rendering
- CLI utilities
- Editor or editor-integration prototypes
- Profiling and performance diagnostics

---

## Phase 6 — Optimization & Scaling

**Status:** ⏳ Planned

### Goals
- Improve performance and memory efficiency
- Validate scalability assumptions

### Planned Topics
- Multithreaded chunk generation
- Streaming optimizations
- Memory usage analysis
- Benchmarking and stress tests

---

## Non-Goals (Explicit)

The following are **not** current priorities:
- Gameplay systems
- AI or NPC logic
- Physics engine
- Networked multiplayer
- User-facing game features

These concerns are expected to live in downstream projects built on top of the engine.

---

## Roadmap Evolution

This roadmap is a living document.

Major changes will be:
- discussed via GitHub Issues or Discussions
- documented through ADRs when architectural impact is significant

---

## Versioning Policy

- Engine core follows **Semantic Versioning**
- API-breaking changes will only occur in major versions
- Experimental features will be introduced via new modules or opt-in APIs