<!-- Allow this file to not have a first line heading -->
<!-- markdownlint-disable-file MD041 no-emphasis-as-heading -->
<!-- markdownlint-disable-file MD033 -->

<div>

# 🧱 voxel-sandbox-engine

**A modern, data-driven voxel engine written in Java**

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
[![Contributor Covenant](https://img.shields.io/badge/contributor%20covenant-v1.4-ff69b4.svg)](CODE_OF_CONDUCT.md)
![Status](https://img.shields.io/badge/status-pre--alpha-orange)

</div>

---

## Overview

Voxel Sandbox Engine is an experimental open-source project aimed at building a modern, scalable, and maintainable voxel-based engine.

The project explores engine design choices that go beyond traditional voxel engines. In particular, it focuses on maintaining a clear separation between engine logic and game content, adopting a data-driven and database-backed world representation, and designing systems that support efficient world streaming while remaining extensible over time.

This repository represents the technical foundation of the engine rather than a finished or playable game.

---

## Project Status

🚧 **Pre-alpha**

The project is currently in an early setup and design phase. At this stage, no rendering system, gameplay mechanics, or world logic are implemented. The primary focus is on establishing repository structure, open-source governance, and solid architectural groundwork.

All engine subsystems and features will be introduced incrementally, with design decisions documented as they are made.

---

## Planned Architecture

The engine is planned to be composed of clearly separated layers, each with a well-defined responsibility.

At its core, the engine will manage application lifecycle concerns, platform abstraction, configuration, and logging. On top of this foundation, a rendering layer is planned, based on OpenGL, with chunk-oriented mesh generation and GPU-friendly data structures.

World representation and persistence will be handled by a dedicated world and data layer. This layer is intended to support a chunk-based voxel model, database-backed persistence, and spatial queries to enable efficient world streaming.

An asset system is also planned, responsible for texture and asset management, data-driven configuration, and support for multiple content packs. None of these components are implemented yet, and their design will evolve as the project progresses.

---

## Non-Goals

At least in its early stages, this project does not aim to replicate an existing commercial game, provide a complete game out of the box, or prioritize visual fidelity over architectural soundness.

The primary objective is to build a solid and extensible technical foundation that can support experimentation and future development.

---

## Roadmap

A high-level roadmap will be introduced once the core architectural decisions are finalized.

Until then, progress is tracked through GitHub Issues and Pull Requests, with significant design choices recorded as Architectural Decision Records (ADR).

---

## Contributing

Community contributions are welcome.

Before contributing, please read the Contributing Guide, the Code of Conduct, and the Security Policy. For larger changes or architectural proposals, opening a discussion or issue before starting implementation is strongly encouraged.

---

## License

This project is licensed under the Apache License, Version 2.0.

See the LICENSE file for details.