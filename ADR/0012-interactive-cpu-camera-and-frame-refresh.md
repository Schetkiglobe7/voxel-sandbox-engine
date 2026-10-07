# ADR 0012: Interactive CPU Camera and Frame Refresh

## Status
Accepted

## Context

The native preview presents a static CPU image. Camera navigation must refresh
that image without recreating the voxel world or tying camera math to GLFW.
The CPU pipeline remains a deterministic reference for future GPU rendering.

## Decision

- `CameraInput` is a window-independent immutable snapshot. A GLFW polling adapter
  translates held keys and right-button cursor drags into snapshots only while
  the window is focused and has a usable framebuffer. Pointer drag state is reset
  on release or focus loss so re-entry does not produce a jump.
- `PreviewCameraController` is mutable demo state, outside the engine and render
  stages. Given explicit input and elapsed time, it emits immutable perspective
  camera snapshots. WASD moves on the horizontal plane relative to yaw; Q/E moves
  along world Y. Arrows or right-drag rotate yaw/pitch, Shift triples speed, and R
  restores the original pose. Combined movement is normalized, pitch is limited
  to 89 degrees, and keyboard elapsed time is capped at 100 ms to bound stalls.
- `CpuWorldRenderer` retains the same read-only engine world across frames.
  World generation and chunk loading happen once before the preview loop. Every
  actual render still uses a fresh strict render frame and camera snapshot.
- `InteractiveCpuPreview` caches pixels until its camera pose changes. Window
  resizing changes presentation only; the reference image stays 320 × 200.
- `CpuImagePresenter.update` reuses the existing texture and direct staging buffer
  with `glTexSubImage2D`. The buffer is released with the presenter. Updated images
  must retain its dimensions.
- The event loop waits briefly for events to avoid spinning, presents cached pixels
  when idle, and does not move or rerender while unfocused or minimized.

## Consequences

Camera input is testable without a display or native libraries. Integration tests
verify changed pixels, exact reset, cached-image reuse, and unchanged world chunks.
The real native smoke additionally uploads moved/reset frames and validates their
framebuffer samples, including after resize.

The demo remains a synchronous CPU reference with a finite preloaded scene. It
provides no collision, streaming, pointer capture, or GPU voxel rendering. Moving
outside loaded regions produces sky; R returns to the initial view. The 100 ms cap
can slow navigation when rendering takes longer than that. Interactive frame rate
is not guaranteed and allocation/performance work is tracked separately.

## References

- [ADR 0010: CPU world image pipeline](0010-cpu-world-image-pipeline.md)
- [ADR 0011: Native window presentation](0011-native-window-and-cpu-image-presentation.md)
- [GLFW input guide](https://www.glfw.org/docs/3.3/input_guide.html)
