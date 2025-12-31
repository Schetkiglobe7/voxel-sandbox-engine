package com.voxelsandox.rendersystem.core.party;

import com.voxelsandbox.rendersystem.core.chunk.IRenderChunkView;
import com.voxelsandbox.rendersystem.core.loop.IRenderLoop;
import com.voxelsandbox.rendersystem.core.world.IRenderWorldView;
import com.voxelsandbox.rendersystem.cpu.context.CpuRenderContext;
import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CpuGpuParityTest {

    @Test
    void renderLoopUsesOnlyRenderTargetContract() {

        CpuRenderTarget target = new CpuRenderTarget(4, 4);
        CpuRenderContext context = new CpuRenderContext(target);

        IRenderWorldView emptyWorld = new IRenderWorldView() {
            @Override
            public List<IRenderChunkView> getRenderableChunks() {
                return List.of();
            }

            @Override
            public int getMinY() { return 0; }

            @Override
            public int getMaxY() { return 0; }
        };

        IRenderLoop loop = new ParityRenderLoop();

        loop.renderFrame(emptyWorld, context);

        assertEquals(0xFF00FF00, target.getPixel(0, 0));
        assertEquals(0xFF00FF00, target.getPixel(1, 1));
        assertEquals(0xFF00FF00, target.getPixel(2, 2));
        assertEquals(0xFF00FF00, target.getPixel(3, 3));
    }
}
