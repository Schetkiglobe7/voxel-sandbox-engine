package com.voxelsandbox.rendersystem.opengl.internal;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import java.util.Objects;

/** Owns one GLFW session and context on its creating thread. Only one native owner may be active. */
public final class OpenGLWindow implements AutoCloseable {
    interface Driver {
        void initialize();
        long create(int width, int height, String title, boolean visible);
        void makeCurrent(long handle);
        void createCapabilities();
        void clearCapabilities();
        void destroy(long handle);
        void terminate();
    }

    private final Driver driver;
    private final Thread owner = Thread.currentThread();
    private long handle;
    private boolean started;

    public OpenGLWindow() { this(new NativeDriver()); }
    OpenGLWindow(Driver driver) { this.driver = Objects.requireNonNull(driver); }

    public void initialize(int width, int height, String title, boolean visible) {
        checkThread();
        if (handle != 0) return;
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Window dimensions must be positive");
        Objects.requireNonNull(title);
        try {
            driver.initialize();
            started = true;
            handle = driver.create(width, height, title, visible);
            if (handle == 0) throw new IllegalStateException("Unable to create OpenGL 3.3 window; check graphics driver and desktop session");
            driver.makeCurrent(handle);
            driver.createCapabilities();
        } catch (RuntimeException | Error failure) {
            try { close(); } catch (RuntimeException | Error cleanup) { failure.addSuppressed(cleanup); }
            throw failure;
        }
    }

    public boolean isInitialized() { checkThread(); return handle != 0; }
    public long handle() {
        checkThread();
        if (handle == 0) throw new IllegalStateException("Window is not initialized");
        return handle;
    }
    private void checkThread() {
        if (Thread.currentThread() != owner) throw new IllegalStateException("OpenGL window must be used on its creating thread");
    }
    @Override public void close() {
        checkThread();
        if (!started) return;
        long oldHandle = handle;
        handle = 0;
        started = false;
        try {
            if (oldHandle != 0) {
                try { driver.clearCapabilities(); }
                finally {
                    try { driver.makeCurrent(0); }
                    finally { driver.destroy(oldHandle); }
                }
            }
        } finally { driver.terminate(); }
    }

    static void configureHints(boolean visible) {
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, visible ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
    }

    private static final class NativeDriver implements Driver {
        private static NativeDriver active;
        private GLFWErrorCallback callback;
        private GLFWErrorCallback previousCallback;
        public void initialize() {
            synchronized (NativeDriver.class) {
                if (active != null) throw new IllegalStateException("Another OpenGL window owns GLFW");
                active = this;
            }
            try {
                callback = GLFWErrorCallback.createPrint(System.err);
                previousCallback = GLFW.glfwSetErrorCallback(callback);
                if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW; a desktop session is required");
            } catch (RuntimeException | Error failure) {
                terminate();
                throw failure;
            }
        }
        public long create(int width, int height, String title, boolean visible) {
            configureHints(visible);
            return GLFW.glfwCreateWindow(width,height,title,0,0);
        }
        public void makeCurrent(long handle) { GLFW.glfwMakeContextCurrent(handle); }
        public void createCapabilities() {
            if (!GL.createCapabilities().OpenGL33) throw new IllegalStateException("OpenGL 3.3 is required");
        }
        public void clearCapabilities() { GL.setCapabilities(null); }
        public void destroy(long handle) { GLFW.glfwDestroyWindow(handle); }
        public void terminate() {
            try { GLFW.glfwTerminate(); }
            finally {
                try {
                    if (callback != null) {
                        GLFW.glfwSetErrorCallback(previousCallback);
                        callback.free();
                        callback = null;
                        previousCallback = null;
                    }
                } finally {
                    synchronized (NativeDriver.class) { if (active == this) active = null; }
                }
            }
        }
    }
}
