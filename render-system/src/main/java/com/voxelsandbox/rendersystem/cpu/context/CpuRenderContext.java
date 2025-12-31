package com.voxelsandbox.rendersystem.cpu.context;

import com.voxelsandbox.rendersystem.core.context.IRenderContext;
import com.voxelsandbox.rendersystem.core.target.IRenderTarget;

import java.util.Objects;


/**
 * CPU-only render context implementation.
 *
 * <p>
 *     This context describes the execution environment for CPU-based rendering
 *     and exposes the primary {@link IRenderTarget} used for output.
 * </p>
 *
 * <p>
 *     It does not manage frame lifecycle directly; that responsibility
 *     belongs to the {@link IRenderTarget} and is orchestrated by the render loop.
 * </p>
 *
 * <p>
 *     This implementation:
 * </p>
 * <ul>
 *     <li> does NOT assume GPU availability </li>
 *     <li> supports headless execution </li>
 *     <li> is suitable for automated tests </li>
 * </ul>
 */
public class CpuRenderContext implements IRenderContext {

    private final IRenderTarget primaryTarget;

    /**
     * Creates a CPU render context with the given render target.
     *
     * @param primaryTarget the render target used for output
     *
     * @throws NullPointerException if {@code primaryTarget} is {@code null}
     */
    public CpuRenderContext(IRenderTarget primaryTarget) {
        this.primaryTarget = Objects.requireNonNull(
                primaryTarget, "primaryTarget must not be null"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return primaryTarget.getWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return primaryTarget.getHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGpuAvailable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderTarget getPrimaryRenderTarget() {
        return primaryTarget;
    }
}
