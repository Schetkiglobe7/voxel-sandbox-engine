package com.voxelsandbox.rendersystem.opengl.context;

import com.voxelsandbox.rendersystem.core.context.IRenderContext;
import com.voxelsandbox.rendersystem.core.target.IRenderTarget;

import java.util.Objects;


/**
 * OpenGL-specific render context implementation.
 *
 * <p>
 *     This class represents the execution context for an OpenGL-backed
 *     rendering pipeline.
 * </p>
 *
 * <p>
 *     A render context describes <strong>how</strong> and <strong>where</strong>
 *     rendering occurs, without performing rendering itself.
 *     In the OpenGL case, this includes:
 * </p>
 * <ul>
 *     <li> declaring GPU availability </li>
 *     <li> exposing the primary OpenGL-backed render target </li>
 *     <li> providing render surface dimensions </li>
 * </ul>
 *
 * <p>
 *     <strong>Important:</strong> This implementation is currently a
 *     <em>structural placeholder</em>.
 * </p>
 *
 * <p>
 *     At this stage:
 * </p>
 * <ul>
 *     <li> no OpenGL context is created </li>
 *     <li> no OpenGL state is accessed </li>
 *     <li> no windowing or platform APIs are involved </li>
 * </ul>
 *
 * <p>
 *     The purpose of this class is to:
 * </p>
 * <ul>
 *     <li> establish a GPU-capable {@link IRenderContext} implementation </li>
 *     <li> enable CPU ↔ GPU render loop parity </li>
 *     <li> prepare the ground for future OpenGL context management </li>
 * </ul>
 *
 * <p>
 *     Actual OpenGL responsibilities (context creation, capabilities,
 *     state tracking) will be introduced incrementally in later steps.
 * </p>
 */
public final class OpenGLRenderContext implements IRenderContext {

    /**
     * Primary render target associated with this context.
     *
     * <p>
     *     In an OpenGL backend, this will typically represent:
     * </p>
     * <ul>
     *     <li> the default window framebuffer, or </li>
     *     <li> an off-screen framebuffer object (FBO) </li>
     * </ul>
     */
    private final IRenderTarget target;

    /**
     * Creates an OpenGL render context using the given render target.
     *
     * @param target the render target used for OpenGL rendering output
     *
     * @throws NullPointerException if {@code target} is {@code null}
     */
    public OpenGLRenderContext(IRenderTarget target) {
        this.target = Objects.requireNonNull(target, "IRenderTarget may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return target.getWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return target.getHeight();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     This implementation always reports GPU availability,
     *     as it represents an OpenGL-backed rendering context.
     * </p>
     */
    @Override
    public boolean isGpuAvailable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderTarget getPrimaryRenderTarget() {
        return target;
    }
}
