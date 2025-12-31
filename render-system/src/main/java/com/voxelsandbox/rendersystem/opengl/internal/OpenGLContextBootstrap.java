package com.voxelsandbox.rendersystem.opengl.internal;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;


/**
 * Manages the lifecycle of a headless OpenGL context using GLFW.
 *
 * <p>
 *     This class is responsible for creating, owning, and destroying
 *     a minimal OpenGL execution environment.
 * </p>
 *
 * <p>
 *     It is strictly <strong>internal</strong> to the OpenGL backend and
 *     MUST NOT be exposed through public APIs or referenced by:
 * </p>
 * <ul>
 *     <li> the engine module </li>
 *     <li> the render-core layer </li>
 *     <li> CPU-based rendering pipelines </li>
 * </ul>
 *
 * <p>
 *     This class performs <strong>no rendering</strong>.
 *     Its sole responsibility is to ensure that a valid OpenGL context
 *     exists and remains alive for the duration of the rendering backend.
 * </p>
 *
 * <p>
 *     The context is created as a hidden (headless) window and is suitable
 *     for:
 * </p>
 * <ul>
 *     <li> off-screen rendering </li>
 *     <li> automated tests </li>
 *     <li> GPU resource initialization </li>
 * </ul>
 */
public final class OpenGLContextBootstrap {

    private long windowHandle = MemoryUtil.NULL;
    private boolean initialized = false;

    /**
     * Initializes the OpenGL context if not already initialized.
     *
     * <p>
     *     This method is <strong>idempotent</strong>:
     *     calling it multiple times has no effect after successful initialization.
     * </p>
     *
     * <p>
     *     The initialization sequence performs the following steps:
     * </p>
     * <ul>
     *     <li> initializes GLFW </li>
     *     <li> configures OpenGL context hints </li>
     *     <li> creates a hidden (1x1) window </li>
     *     <li> makes the OpenGL context current </li>
     *     <li> initializes OpenGL capabilities via LWJGL </li>
     * </ul>
     *
     * <p>
     *     No rendering commands are executed here.
     * </p>
     *
     * @throws IllegalStateException if GLFW initialization or window creation fails
     */
    public void initialize() {
        if (initialized) {
            return;
        }

        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize OpenGL GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        windowHandle = GLFW.glfwCreateWindow(
                1,
                1,
                "headless",
                MemoryUtil.NULL,
                MemoryUtil.NULL
        );

        if (windowHandle == MemoryUtil.NULL) {
            GLFW.glfwTerminate();
            throw new IllegalStateException("Failed to create GLFW window");
        }

        GLFW.glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        initialized = true;
    }

    /**
     * Destroys the OpenGL context and releases all native resources.
     *
     * <p>
     *     This method:
     * </p>
     * <ul>
     *     <li> destroys the GLFW window </li>
     *     <li> terminates GLFW </li>
     *     <li> resets internal state </li>
     * </ul>
     *
     * <p>
     *     Calling this method when the context is not initialized
     *     has no effect.
     * </p>
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }
        GLFW.glfwDestroyWindow(windowHandle);
        GLFW.glfwTerminate();

        windowHandle = MemoryUtil.NULL;
        initialized = false;
    }

    /**
     * Returns whether the OpenGL context has been successfully initialized.
     *
     * <p>
     *     A value of {@code true} indicates that:
     * </p>
     * <ul>
     *     <li> GLFW has been initialized </li>
     *     <li> a window and OpenGL context exist </li>
     *     <li> OpenGL capabilities are available </li>
     * </ul>
     *
     * @return {@code true} if the OpenGL context is active, {@code false} otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }
}
