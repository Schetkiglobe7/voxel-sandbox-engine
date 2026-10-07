<!-- Allow this file to not have a first line heading -->
<!-- markdownlint-disable-file MD041 no-emphasis-as-heading -->
<!-- markdownlint-disable-file MD033 -->

<div>

# 🧱 voxel-sandbox-engine

**A modern, data-driven voxel engine written in Java**

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
[![Contributor Covenant](https://img.shields.io/badge/contributor%20covenant-v1.4-ff69b4.svg)](CODE_OF_CONDUCT.md)
![Status](https://img.shields.io/badge/status-engine%20pre--release%201.1.0-blue)

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
The core engine module is implemented and released as a **pre-release (v1.1.0)**,
The experimental `render-system` module includes CPU frame pipelines, camera ray
generation, voxel traversal, and OpenGL context scaffolding. Persistence and
tooling remain planned.

---

## Project Status

🧊 **Engine Core: Pre-release v1.1.0**

The engine core has reached its first stable architectural milestone.

Starting from `engine-v1.1.0`, the **public engine API is considered frozen**
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
- CPU rendering pipeline with concrete ray traversal and PNG world preview

### Not Implemented Yet
- Complete GPU renderer (the OpenGL render loop remains a placeholder)
- Persistence layer (database-backed world storage)
- Asset pipeline
- Gameplay systems
- Tooling and editor support

---

## Build and Run

Install a **JDK 17** matching your machine architecture and set `JAVA_HOME`.
The Gradle Wrapper downloads Gradle and Maven dependencies on the first build.
Node.js is optional and used only for contribution hooks.

macOS / Linux:

```sh
./gradlew build
./gradlew :engine:run
```

Windows (PowerShell):

```powershell
.\gradlew.bat build
.\gradlew.bat :engine:run
```

The engine executable is a headless bootstrap and prints
`Voxel Sandbox Engine bootstrap OK`. The rendering demo generates a world image:

```sh
./gradlew :render-system:run
# Optional output path (relative to the render-system directory):
./gradlew :render-system:run --args="build/demo/custom-world.png"
```

On Windows use `.\gradlew.bat :render-system:run`.
The default output is `render-system/build/demo/voxel-world.png` (320 × 200 pixels).
The scene contains a flat voxel world and a stepped structure. Camera rays, DDA
traversal, and diagnostic face shading run through the strict frame pipeline.
The demo requires no display or GPU and does not open a window.

### Native Window Preview

To display the CPU world image in a resizable OpenGL window on macOS/Linux:

```sh
./gradlew :render-system:runPreview
```

Windows (PowerShell):

```powershell
.\gradlew.bat :render-system:runPreview
```

Close with **Escape** or the window close button. The scene and camera are static;
OpenGL presents the CPU image as a texture. This is not yet GPU voxel rendering.
It requires a desktop session and OpenGL 3.3 or later. The Gradle task automatically
adds `-XstartOnFirstThread` on macOS; no manual JVM flags are needed.

For a bounded native verification:

```sh
./gradlew :render-system:nativeSmoke
```

This creates hidden windows, checks initialization/shutdown/restart, presents the
world image, compares framebuffer samples to CPU pixels before and after resizing, and exits. Its readback
is saved to `render-system/build/demo/opengl-world.png`. On Linux without a desktop,
install Xvfb and Mesa and run `xvfb-run -a ./gradlew :render-system:nativeSmoke`.
The regular tests and PNG demo remain display-free.


Use `:render-system:installDist` to generate the rendering demo launchers and
`:render-system:distZip` for an archive. Run `render-system` or `render-system.bat`
from `render-system/build/install/render-system/bin`, optionally passing a PNG path.
For installed launchers, relative output paths resolve against the current directory.

Use `:engine:installDist` to generate launchers under `engine/build/install/engine/bin`;
these require Java 17 at runtime. `:engine:distZip` creates a distributable archive.

Native rendering dependencies are selected for the host OS and JVM architecture:
macOS, Windows, and Linux support x86-64 and ARM64. Distributions of a future
native renderer must be built separately for each target. The CI matrix builds,
tests, runs both headless demos, and uploads a CPU preview on all three systems.

CPU tests require no display or OpenGL driver. GLFW context creation requires a
desktop session and an OpenGL driver; a hidden window is not a display-free context.
On macOS, GLFW launchers must pass `-XstartOnFirstThread` as described in the
[LWJGL guide](https://www.lwjgl.org/guide). OpenGL contexts request the
[forward-compatible core profile](https://www.glfw.org/docs/3.3/window_guide.html).

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

### Rendering Layer (In Progress)

The experimental `render-system` module is being developed to:
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

Engine APIs are considered stable starting from `engine-v1.1.0`.
Any breaking change requires a major version bump.

---

## License

This project is licensed under the Apache License, Version 2.0.

See the LICENSE file for details.