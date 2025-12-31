<!-- markdownlint-disable blanks-around-headings blanks-around-lists no-duplicate-heading -->

# Changelog

All notable changes to this project will be documented in this file.

This project follows [Keep a Changelog](https://keepachangelog.com/)
and adheres to [Semantic Versioning](https://semver.org/).

---

## [Unreleased]

Planned and ongoing work after the first engine pre-release.

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