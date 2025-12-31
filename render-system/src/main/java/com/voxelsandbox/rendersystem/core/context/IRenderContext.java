package com.voxelsandbox.rendersystem.core.context;


import com.voxelsandbox.rendersystem.core.target.IRenderTarget;

/**
 * Describes the execution context in which rendering occurs.
 *
 * <p>
 *     A render context encapsulates enviromental and configuration
 *     information required to render a frame, independently of the
 *     rendering backend.
 * </p>
 *
 * <p>
 *     The context coordinates one or more {@link IRenderTarget}s
 *     and defines the lifecycle of a render frame.
 * </p>
 *
 * <p>
 *     This interface is intentionally backend-agnostic:
 * </p>
 *
 * <ul>
 *     <li> no GPU or OpenGL assumptions </li>
 *     <li> no windowing or platform APIs </li>
 *     <li> no threading or timing semantics </li>
 * </ul>
 *
 * <p>
 *     Render contexts are expected to be immutable and reusable.
 * </p>
 */
public interface IRenderContext {

    /**
     * Width of the render target in pixels.
     *
     * @return render width
     */
    int getWidth();

    /**
     * Height of the render target in pixels.
     *
     * @return render height
     */
    int getHeight();

    /**
     * Returns wether this context targets a GPU-backend renderer.
     *
     * <p>
     *     This flag allows render loops to adapt behavior
     *     without binding to a specific graphics API.
     * </p>
     *
     * @return {@code true} if GPU-backend, {@code false} otherwise
     */
    boolean isGpuAvailable();

    /**
     * Returns the primary render target.
     *
     * @return primary render target
     */
    IRenderTarget getPrimaryRenderTarget();
}
