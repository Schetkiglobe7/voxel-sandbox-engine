# Architectural Decision Records (ADR)

This directory contains the **Architectural Decision Records (ADR)** for the
`voxel-sandbox-engine` project.

ADRs document **significant architectural decisions**, the context in which they
were made, and their consequences. They serve as a long-term memory of why the
engine is designed the way it is.

---

## What is an ADR?

An Architectural Decision Record is a short document that captures:

- the **context** of a problem or design choice
- the **decision** that was made
- the **consequences** of that decision
- possible **trade-offs** and limitations

ADRs help prevent architectural drift, repeated discussions, and implicit
assumptions that are lost over time.

---

## Why ADRs in this project?

The `voxel-sandbox-engine` is designed as a **long-lived, extensible engine core**.

Given the complexity of:
- world streaming
- chunk lifecycle management
- future rendering backends
- persistence and scalability concerns

it is critical to make architectural decisions **explicit, reviewable, and
traceable**.

ADRs ensure that:
- design choices are intentional
- future contributors understand past decisions
- breaking changes are justified and documented

---

## ADR Lifecycle

Each ADR has a **status**, typically one of:

- **Proposed** – under discussion
- **Accepted** – approved and implemented
- **Deprecated** – no longer recommended
- **Superseded** – replaced by a newer ADR

Once accepted, an ADR should not be modified except for:
- status updates
- links to superseding ADRs
- clarifying notes (without changing intent)

Each ADR is immutable once accepted, except for status changes or references
to superseding decisions

---

## Naming Convention

ADRs follow this naming scheme:

NNNN-short-decision-title.md

Where:
- `NNNN` is a zero-padded incremental number (e.g. `0001`)
- `short-decision-title` is a kebab-case summary

Example:

0001-engine-scope.md

---

## When to Create a New ADR

Create an ADR when making decisions that affect:

- engine scope or responsibilities
- public API guarantees
- world or chunk model semantics
- streaming, eviction, or lifecycle rules
- persistence or storage strategy
- rendering backend abstraction
- performance vs correctness trade-offs

If a decision can impact **future compatibility or architecture**, it deserves an ADR.

---

## Relationship with Code

ADRs are **authoritative** with respect to architectural intent.

Code changes that contradict an accepted ADR must either:
- update the ADR status, or
- introduce a new ADR that supersedes it

---

## Current ADR Index

| ID   | Title                                   | Status   |
|----:|-----------------------------------------|----------|
| 0001 | Engine Scope and Responsibilities       | Accepted |

---

## Notes

ADRs are written in Markdown and kept intentionally concise.

They are not design documents or tutorials, but **decision records**.