package com.voxelsandbox.rendersystem.core.loop;

import com.voxelsandbox.rendersystem.core.context.IRenderContext;
import com.voxelsandbox.rendersystem.core.world.IRenderWorldView;


/**
 * Defines the conceptual render loop of the rendering system.
 *
 * <p>
 *     A render loop represents the high-level execution unit responsible
 *     for producing visual output from a read-only render view of the world.
 * </p>
 *
 * <p>
 *     The render loop operates on two distinct inputs:
 * </p>
 * <ul>
 *     <li>
 *         an {@link IRenderWorldView}, representing a read-only snapshot
 *         of the world to be rendered
 *     </li>
 *     <li>
 *         an {@link IRenderContext}, representing the rendering environment
 *         and output target (CPU buffer, GPU framebuffer, test surface, etc.)
 *     </li>
 * </ul>
 *
 * <p>
 *     This interface is intentionally minimal and backend-agnostic:
 * </p>
 * <ul>
 *     <li> it does NOT assume GPU availability </li>
 *     <li> it does NOT depend on OpenGL or any graphics API </li>
 *     <li> it does NOT manage timing, threads, or scheduling </li>
 * </ul>
 *
 * <p>
 *     The render loop is invoked externally (e.g. by the engine or application)
 *     and processes a single frame at a time.
 * </p>
 *
 * <p>
 *     Implementations MUST:
 * </p>
 * <ul>
 *     <li> treat the provided {@link IRenderWorldView} as read-only </li>
 *     <li> respect the constraints expressed by {@link IRenderContext} </li>
 *     <li> avoid mutating engine or world state </li>
 *     <li> be deterministic for the same inputs </li>
 * </ul>
 */
public interface IRenderLoop {

    /**
     * Renders a single frame using the provided render-world view
     * and rendering context.
     *
     * <p>
     *     This method represents one iteration of the render loop.
     *     It does not imply real-time constraints or continuous execution.
     * </p>
     *
     * <p>
     *     The caller is responsible for:
     * </p>
     * <ul>
     *     <li> deciding when this method is invoked </li>
     *     <li> providing a consistent world snapshot </li>
     *     <li> managing timing, synchronization, and frame pacing </li>
     * </ul>
     *
     * @param worldView a read-only view of the world for rendering
     * @param context the rendering context defining environment and output target
     *
     * @throws NullPointerException if {@code worldView} or {@code context} is {@code null}
     */
    void renderFrame(IRenderWorldView worldView, IRenderContext context);
}
