package com.voxelsandbox.rendersystem.opengl.internal;

/** Hidden OpenGL context owner. Requires a desktop session even though the window is hidden. */
public final class OpenGLContextBootstrap implements AutoCloseable {
    private final OpenGLWindow window = new OpenGLWindow();
    public void initialize() { window.initialize(1,1,"Voxel context smoke test",false); }
    public void shutdown() { window.close(); }
    public boolean isInitialized() { return window.isInitialized(); }
    @Override public void close() { shutdown(); }
}
