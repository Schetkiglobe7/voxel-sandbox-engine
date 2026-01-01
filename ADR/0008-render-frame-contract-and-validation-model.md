# ADR 0008 — RenderFrame Contract & Validation Model

## Status
Accepted

## Context

As the rendering system evolved beyond simple ray traversal (STEP 1.7),
the engine required a robust mechanism to:

- share intermediate data between render stages
- enforce strict stage input/output contracts
- detect invalid pipeline behavior early
- remain backend-agnostic and future-proof (CPU now, GPU later)

Modern rendering APIs (e.g. Vulkan, modern render graphs) treat a frame
as an explicit data flow container rather than an implicit global state.
The engine needed a similar concept.

## Decision

We introduce the concept of a **RenderFrame** as a frame-scoped,
mutable data container with explicit validation rules.

A concrete CPU implementation, `CpuRenderFrame`, is provided as a
reference and validation baseline.

Key decisions:

1. **RenderFrame as an interface**
    - Backend-agnostic
    - Enables future GPU or multi-threaded implementations

2. **Explicit stage lifecycle**
    - `beginStage(stageId, inputs, outputs)`
    - `put(...)`, `get(...)`
    - `validateAfterStage(stageId)`
    - `validateEndOfFrame()`

3. **Explicit input/output contracts**
    - Stages must declare required inputs and produced outputs
    - Undeclared reads and writes are rejected

4. **Single-write rule**
    - Each FrameKey may be written exactly once per frame

5. **Validation modes**
    - `STRICT`: violations throw exceptions
    - `RELAXED`: violations are ignored (tooling / inspection use cases)

6. **Debug-only consistency assertions**
    - Implemented using `assert`
    - Enabled only with `-ea`
    - Detects engine misuse during development

7. **Frame inspection utilities**
    - Dump stored keys
    - Dump executed stages
    - Dump per-stage written outputs

## Consequences

### Positive
- Strong guarantees about pipeline correctness
- Early detection of stage contract violations
- Deterministic and debuggable render execution
- Architecture aligned with modern render graph design
- Clean separation between:
    - pipeline orchestration
    - stage logic
    - frame data ownership

### Trade-offs
- Slightly more boilerplate for render stage definitions
- Runtime checks add overhead (acceptable for CPU reference path)
- Requires discipline when defining stage contracts

### Non-goals
- Automatic dependency resolution
- Parallel stage scheduling
- GPU resource management
- Render graph optimization

These concerns are explicitly deferred to later milestones.

## Related Decisions
- ADR 0005 — Engine module boundaries and API stability
- STEP 1.7 — Deterministic voxel ray traversal
- STEP 1.8 — Render pipeline architecture

## Next Steps
- STEP 1.8.D — First concrete render stages
- STEP 1.9 — Render graph abstraction (optional)
- STEP 2.x — GPU backend parity