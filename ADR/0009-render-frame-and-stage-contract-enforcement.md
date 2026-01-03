# ADR-0009 — RenderFrame & Stage Contract Enforcement

## Status
Accepted

## Context

During Milestone 1.8, the render system introduced a deterministic,
stage-based pipeline built around a shared `RenderFrame`.

Each render stage:
- declares required input keys
- declares produced output keys
- operates exclusively on frame-scoped data

Without strict enforcement, the following failure modes were possible:
- stages reading undeclared or missing inputs
- stages writing undeclared outputs
- stages writing outside of execution boundaries
- silent overwrites of frame data
- invalid stage ordering producing undefined behavior

These issues would become critical blockers for:
- GPU pipeline parity
- render graph validation
- debugging and tooling
- deterministic execution guarantees

## Decision

The render system enforces **strict stage–frame contracts** at runtime,
using the `RenderFrame` as the central authority.

The following rules are enforced:

### 1. Stage Execution Boundaries
- A stage must explicitly signal execution start via `beginStage`
- Writes outside an active stage are forbidden
- A frame may only execute one stage at a time

### 2. Declared Inputs Enforcement
- All required input keys must exist in the frame before stage execution
- Reads of undeclared inputs are rejected (in strict mode)
- Missing inputs cause immediate failure

### 3. Declared Outputs Enforcement
- A stage may only write keys declared in `getProducedOutputs`
- Each output key may be written exactly once per frame
- All declared outputs must be written before stage completion

### 4. Stage Ordering Enforcement
- Stage execution order is explicit and deterministic
- The pipeline does not reorder or resolve dependencies
- Invalid ordering is detected via missing required inputs

### 5. Validation Modes
Two validation modes are supported:
- `STRICT`: violations throw exceptions immediately
- `RELAXED`: violations are ignored (intended for prototyping only)

The default mode is `STRICT`.

### 6. Debug & Inspection Support
The frame exposes debug-only inspection hooks:
- executed stage order
- produced keys per stage
- current frame key set

These are intended for tooling, debugging and future render graph visualization.

## Consequences

### Positive
- Fail-fast detection of pipeline misuse
- Deterministic and debuggable execution
- Strong alignment with modern render graph semantics
- GPU-friendly mental model (Vulkan-style)
- Clear ownership and data flow rules

### Negative
- Slight runtime overhead in debug/CPU mode
- Higher upfront discipline required when writing stages
- No automatic dependency resolution (by design)

## Alternatives Considered

### Automatic Stage Reordering
Rejected.
Implicit dependency resolution hides errors and complicates debugging.

### Weak Enforcement (warnings only)
Rejected.
Silent failures are unacceptable for a deterministic render system.

### External Render Graph System
Deferred.
The current design intentionally mirrors render graphs
while remaining minimal and CPU-first.

## Notes

This ADR establishes the foundation required for:
- geometry and ray generation stages
- GPU backend integration
- render graph extraction
- frame capture and debugging tools