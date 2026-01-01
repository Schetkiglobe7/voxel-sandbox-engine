# ADR 0006: Render Stage Contract and Frame-Driven Pipeline

## Status
Accepted

## Context

As part of STEP 1.8, the render system introduces a deterministic,
frame-driven rendering architecture inspired by modern render pipelines
and render graph concepts.

To enable predictable execution, validation, debugging and future
parallelization, each render stage must expose a clear and enforceable
contract describing:

- which data it consumes
- which data it produces
- how it is identified within the pipeline

This decision formalizes the responsibilities and invariants of a render
stage and defines how stages interact with a shared RenderFrame.

## Decision

We define a strict render stage contract via the `IRenderStage` interface
with the following characteristics:

- Each render stage is **stateless**, **deterministic** and **reentrant**
- Each stage declares:
    - a stable unique identifier (`getId()`)
    - a set of required input keys (`getRequiredInputs()`)
    - a set of produced output keys (`getProducedOutputs()`)
- All data exchange between stages occurs exclusively through `RenderFrame`
- Stages may only read declared inputs and must write all declared outputs
- Any access outside the declared contract is considered invalid behavior

The render pipeline (`IRenderPipeline`) is responsible for:

- enforcing execution order
- invoking frame validation hooks
- owning the frame lifecycle
- remaining agnostic of rendering APIs and resources

## Consequences

### Positive
- Enables deterministic, testable render pipelines
- Makes data dependencies explicit and inspectable
- Allows early validation and fail-fast behavior
- Prepares the system for future render graph validation
- Provides a stable foundation for CPU and GPU pipeline parity

### Trade-offs
- Slightly more verbose stage definitions
- Requires discipline in declaring inputs and outputs explicitly

## Alternatives Considered

### Implicit stage dependencies
Rejected due to poor debuggability and inability to validate correctness.

### Pipeline-managed dependency resolution
Deferred to a future render graph layer; current scope focuses on a linear,
deterministic pipeline.

## Notes

This ADR intentionally avoids introducing scheduling, parallelism or
dependency resolution. These concerns are expected to be addressed in
future iterations once the render graph layer is introduced.