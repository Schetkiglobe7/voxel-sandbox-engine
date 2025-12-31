package com.voxelsandbox.rendersystem.cpu.loop;


import com.voxelsandbox.rendersystem.core.context.IRenderContext;
import com.voxelsandbox.rendersystem.core.loop.IRenderLoop;
import com.voxelsandbox.rendersystem.core.target.IRenderTarget;
import com.voxelsandbox.rendersystem.core.world.IRenderWorldView;

import java.util.Objects;


/**
 * Minimal CPU-only render loop implementation.
 *
 * <p>
 *     This render loop validates the rendering pipeline without
 *     relying on GPU acceleration or graphics APIs.
 * </p>
 *
 * <p>
 *     It is intended for:
 * </p>
 * <ul>
 *     <li> architectural validation </li>
 *     <li> automated testing </li>
 *     <li> headless execution </li>
 * </ul>
 */
public final class CpuRenderLoop implements IRenderLoop {

    @Override
    public void renderFrame(IRenderWorldView worldView, IRenderContext context) {
        Objects.requireNonNull(worldView, "worldView must not be null");
        Objects.requireNonNull(context, "context must not be null");

        IRenderTarget target = context.getPrimaryRenderTarget();
        Objects.requireNonNull(target, "render target must not be null");

        target.beginFrame();

        // Iteration only – no actual rendering yet
        worldView.getRenderableChunks().forEach(chunk -> {
            // placeholder for future rendering logic
        });

        target.endFrame();
    }
}
