package com.voxelsandox.rendersystem.cpu;

import com.voxelsandbox.rendersystem.core.context.IRenderContext;
import com.voxelsandbox.rendersystem.core.loop.IRenderLoop;
import com.voxelsandbox.rendersystem.core.world.IRenderWorldView;
import com.voxelsandbox.rendersystem.core.chunk.IRenderChunkView;
import com.voxelsandbox.rendersystem.cpu.context.CpuRenderContext;
import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Minimal render loop used only for validating the CPU render pipeline.
 *
 * <p>
 *     This implementation deliberately ignores the world content and
 *     writes a deterministic pixel to the render target.
 * </p>
 */
final class DummyRenderLoop implements IRenderLoop {

    @Override
    public void renderFrame(IRenderWorldView worldView, IRenderContext context) {
        // Contract checks
        if (worldView == null || context == null) {
            throw new NullPointerException();
        }

        CpuRenderTarget target =
                (CpuRenderTarget) context.getPrimaryRenderTarget();

        // Simulate a frame
        target.beginFrame();

        // Write a known pixel (ARGB: opaque red)
        target.setPixel(0, 0, 0xFFFF0000);

        target.endFrame();
    }
}

public class CpuRenderPipelineSmokeTest {

    @Test
    void cpuRenderPipelineWritesPixels() {

        // Arrange
        CpuRenderTarget target = new CpuRenderTarget(4, 4);
        CpuRenderContext context = new CpuRenderContext(target);

        // Minimal world stub (no chunks)
        IRenderWorldView emptyWorld = new IRenderWorldView() {

            @Override
            public Collection<IRenderChunkView> getRenderableChunks() {
                return List.of();
            }

            @Override
            public int getMinY() {
                return 0;
            }

            @Override
            public int getMaxY() {
                return 0;
            }
        };

        IRenderLoop loop = new DummyRenderLoop();

        // Act
        loop.renderFrame(emptyWorld, context);

        // Assert
        int pixel = target.getPixel(0, 0);

        assertEquals(
                0xFFFF0000,
                pixel,
                "CPU render pipeline must write expected pixel to render target"
        );
    }
}