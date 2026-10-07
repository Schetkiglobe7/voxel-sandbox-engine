# ADR 0011: Native Window Lifecycle and CPU Image Presentation

## Status
Accepted

## Context

A real native context and a visible preview are needed before implementing a GPU
voxel renderer. The previous context bootstrap did not clean up a window if
capability initialization failed. GLFW termination and thread-local capabilities
also require explicit ownership.

## Decision

- `OpenGLWindow` owns one GLFW session and window on its creating thread. The
  native driver rejects another active owner. This is a single-window baseline;
  shared contexts and external GLFW sessions are outside this lifecycle contract.
- Initialization and shutdown are idempotent and a closed owner may be restarted.
  Partial initialization destroys any created window and terminates the session.
  Shutdown clears capabilities, detaches the current context, destroys the window,
  restores the prior error callback, and frees the installed callback.
- Context creation requests OpenGL 3.3 core with forward compatibility. macOS
  native Gradle launch tasks supply `-XstartOnFirstThread`.
- Both bootstrap helpers use this owner. `OpenGLContextBootstrap` creates a hidden
  window and supports try-with-resources. The legacy static bootstrap requires
  `shutdown()` and rejects repeated initialization until its session is released.
- `CpuImagePresenter` uploads a CPU target as RGBA and draws a fullscreen triangle
  using a GLSL 330 shader. The CPU image remains the reference; the GPU only presents
  it. Framebuffer dimensions preserve aspect ratio on resize and high-DPI screens.
- Shader, texture, VAO, window, and session resources are released in reverse
  ownership order, including failure paths. No native calls enter the engine or
  CPU rendering stages.
- Native verification is separate from `test`: driver doubles exercise failures
  without a display; `nativeSmoke` uses real contexts and compares framebuffer
  samples against CPU pixels before recording a PNG readback.

## Consequences

The visible preview supports resizing and Escape/window-close exit, but the camera
and scene are static. Camera input and GPU voxel rendering remain future work.
Hidden windows still require a desktop/display service. Linux CI uses Xvfb and
Mesa software rendering; local macOS hardware validation is recorded separately.
Windows native validation remains pending a suitable desktop and graphics driver.

## References

- [GLFW context ownership](https://www.glfw.org/docs/3.3/context_guide.html)
- [GLFW window and context hints](https://www.glfw.org/docs/3.3/window_guide.html)
- [LWJGL macOS launch guidance](https://www.lwjgl.org/guide)
