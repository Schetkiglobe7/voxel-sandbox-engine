package com.voxelsandbox.rendersystem.opengl.internal;

/** Legacy bootstrap facade; callers must release the owned session with shutdown(). */
public final class OpenGLBootstrap {
    private static OpenGLWindow window;
    private OpenGLBootstrap() {}
    public static long initWindow(int width,int height,String title) {
        if (window != null) throw new IllegalStateException("Bootstrap window already exists; call shutdown first");
        var candidate = new OpenGLWindow();
        candidate.initialize(width,height,title,false);
        window = candidate;
        return window.handle();
    }
    public static void shutdown() {
        if (window == null) return;
        window.close();
        window = null;
    }
}
