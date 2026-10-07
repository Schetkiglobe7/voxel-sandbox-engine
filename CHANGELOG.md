<!-- markdownlint-disable blanks-around-headings blanks-around-lists no-duplicate-heading -->

# Changelog

All notable changes to this project will be documented in this file.

This project follows [Keep a Changelog](https://keepachangelog.com/)
and adheres to [Semantic Versioning](https://semver.org/).

---

## [Unreleased]

Planned and ongoing work after the first engine pre-release.

### Added
- Headless CPU world rendering demo with PNG export and portable application launchers.
- Scene input and diagnostic pixel output stages composing the strict frame pipeline.
- Integration tests for deterministic pixel output, PNG round trips, and negative coordinates.
- CPU preview artifacts from the macOS, Windows, and Linux CI matrix.
- ADR 0010 documenting the CPU world image pipeline and its scope.
- Concrete CPU ray batch traversal stage with frame-scoped world and distance inputs.
- Read-only engine voxel adapter and traversal/batch regression coverage.
- Portable headless engine launcher and distributable launch scripts.
- CPU ARGB render target and backend-independent target contract.
- Build, test, and headless startup CI matrix for macOS, Windows, and Linux.

### Fixed
- DDA hit normals when a ray starts inside solid voxels.
- DDA visitor entry distances and traversal distance limits.
- Missing Gradle Wrapper JAR in clean checkouts.
- Missing render target classes preventing rendering module compilation.
- Native dependency selection for host OS and x86-64/ARM64 architecture.
- OpenGL core context forward compatibility required on macOS.
- Source target packages accidentally excluded by the Maven ignore rule.

### Changed
- README, architecture, and roadmap now describe implemented rendering foundations
  and the remaining CPU/GPU work.

### Planned
- Rendering layer (OpenGL, Vulkan evaluation)
- GPU-friendly chunk meshing
- Persistence layer (database-backed world storage)
- Asset and content pipeline
- Tooling and editor support
- Performance profiling and benchmarks

---

## [engine-v1.0.0] – Pre-release

This pre-release marks the **first complete and test-covered version of the engine core**.
The public API of the engine module is considered **frozen starting from this release**.

### Added
- Core voxel world engine module
- Chunk-based world representation
- Deterministic world generation based on seed
- Chunk lifecycle management (generation, loading, unloading)
- Safe world bounds handling for voxel access
- Event-driven world notifications:
    - `onChunkGenerated`
    - `onChunkLoaded`
    - `onChunkUnloaded`
- Pluggable chunk eviction policy system
- Distance-based chunk eviction policy
- Fuzzy distance-based chunk eviction policy (tanh-based)
- Chunk streaming controllers:
    - Distance-based streaming controller
    - Fuzzy distance streaming controller
- Deterministic and idempotent streaming updates
- Extensive unit test coverage for:
    - World state behavior
    - Chunk lifecycle events
    - Streaming controllers
    - Eviction policies

### Changed
- Repository structure stabilized around the engine core
- Engine APIs documented and frozen

### Deprecated
- N/A

### Removed
- N/A

### Fixed
- N/A

---

<!-- links -->
[Unreleased]: https://github.com/<YOUR_GITHUB_ORG>/voxel-sandbox-engine/compare/engine-v1.0.0...HEAD
[engine-v1.0.0]: https://github.com/<YOUR_GITHUB_ORG>/voxel-sandbox-engine/releases/tag/engine-v1.0.0