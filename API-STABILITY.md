# API Stability Policy

This module follows Semantic Versioning.

## Scope

Starting from `engine-v1.0.0`, all public APIs of the engine module are considered **stable**.
This includes all public classes, interfaces, and methods exposed under the `engine` namespace.

## Stability Guarantees

- Public APIs will not change in a backward-incompatible way without a **major version bump**
- Internal packages and implementation details are **not covered** by this guarantee
- Experimental APIs (if any) are explicitly marked and may change without notice

## Versioning Rules

- **MAJOR**: breaking API changes
- **MINOR**: backward-compatible features
- **PATCH**: bug fixes and internal improvements

## Exceptions

Backward-incompatible changes may be introduced without a major bump only to address
critical security issues or data corruption bugs.