package com.voxelsandbox.rendersystem.opengl.internal;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;


/**
 * Low-level OpenGL and GLFW bootstrap utility.
 *
 * <p>
 *     This class is responsible for initializing the minimal OpenGL
 *     execution environment required by the rendering backend.
 * </p>
 *
 * <p>
 *     Its responsibilities are intentionally narrow and strictly limited to:
 * </p>
 * <ul>
 *     <li> initializing GLFW </li>
 *     <li> creating a native window </li>
 *     <li> creating an OpenGL context </li>
 *     <li> initializing OpenGL capabilities via LWJGL </li>
 * </ul>
 *
 * <p>
 *     <strong>This class is INTERNAL to the OpenGL backend.</strong>
 * </p>
 *
 * <p>
 *     It MUST NOT:
 * </p>
 * <ul>
 *     <li> be referenced by engine or render-core modules </li>
 *     <li> contain rendering logic </li>
 *     <li> manage frame lifecycle or render loops </li>
 *     <li> expose OpenGL state to higher layers </li>
 * </ul>
 *
 * <p>
 *     The existence of this class allows the rest of the rendering
 *     system to remain fully decoupled from GLFW and OpenGL setup details.
 * </p>
 */
public final class OpenGLBootstrap {


    /**
     * Utility class – no instances allowed.
     */
    private OpenGLBootstrap() {}

    /**
     * Initializes GLFW, creates a hidden OpenGL window,
     * and initializes OpenGL capabilities.
     *
     * <p>
     *     The created window is hidden by default and intended
     *     to serve as a rendering surface or context owner.
     * </p>
     *
     * <p>
     *     This method performs the following steps:
     * </p>
     * <ol>
     *     <li> initializes GLFW </li>
     *     <li> configures basic window hints </li>
     *     <li> creates a native window </li>
     *     <li> makes the OpenGL context current </li>
     *     <li> initializes OpenGL capabilities via {@link GL#createCapabilities()} </li>
     * </ol>
     *
     * <p>
     *     No rendering is performed here.
     * </p>
     *
     * @param width window width in pixels
     * @param height window height in pixels
     * @param title window title
     *
     * @return the native GLFW window handle
     *
     * @throws IllegalStateException if GLFW cannot be initialized
     * @throws RuntimeException if window creation fails
     */
    public static long initWindow(int width, int height, String title) {
        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        long window = GLFW.glfwCreateWindow(
                width,
                height,
                title,
                MemoryUtil.NULL,
                MemoryUtil.NULL
        );

        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        return window;
    }
}
