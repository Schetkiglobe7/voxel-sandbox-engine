package com.voxelsandbox.rendersystem.opengl.loop;

import com.voxelsandbox.rendersystem.core.context.IRenderContext;
import com.voxelsandbox.rendersystem.core.loop.IRenderLoop;
import com.voxelsandbox.rendersystem.core.world.IRenderWorldView;


/**
 * OpenGL-backed render loop implementation.
 *
 * <p>
 *     This class represents the GPU-based counterpart of the render loop
 *     abstraction, responsible for driving rendering operations using
 *     an OpenGL backend.
 * </p>
 *
 * <p>
 *     It consumes:
 * </p>
 * <ul>
 *     <li> a read-only {@link IRenderWorldView} provided by the engine </li>
 *     <li> an {@link IRenderContext} configured for GPU rendering </li>
 * </ul>
 *
 * <p>
 *     At this stage, this implementation is a <strong>structural placeholder</strong>.
 *     No OpenGL calls or rendering logic are executed yet.
 * </p>
 *
 * <p>
 *     The presence of this class serves to:
 * </p>
 * <ul>
 *     <li> validate the CPU/GPU render loop parity </li>
 *     <li> lock down the render-loop contract early </li>
 *     <li> provide a stable extension point for future OpenGL rendering </li>
 * </ul>
 *
 * <p>
 *     This class MUST NOT:
 * </p>
 * <ul>
 *     <li> mutate engine or world state </li>
 *     <li> perform chunk loading or streaming </li>
 *     <li> manage windowing or OpenGL context creation </li>
 * </ul>
 */
public final class OpenGLRenderLoop implements IRenderLoop {

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, until OpenGL rendering
     *         logic is introduced in a later development step
     */
    @Override
    public void renderFrame(IRenderWorldView worldView, IRenderContext context) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
