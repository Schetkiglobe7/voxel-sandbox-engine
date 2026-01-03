package com.voxelsandox.rendersystem.core.parity;

import com.voxelsandbox.rendersystem.core.context.IRenderContext;
import com.voxelsandbox.rendersystem.core.loop.IRenderLoop;
import com.voxelsandbox.rendersystem.core.world.IRenderWorldView;


class ParityRenderLoop implements IRenderLoop {
    @Override
    public void renderFrame(IRenderWorldView world, IRenderContext context) {
        var target = context.getPrimaryRenderTarget();
        target.beginFrame();
        for (int i = 0; i < 4; i++) {
            target.drawPixel(i, i, 0xFF00FF00);
        }
        target.endFrame();
    }
}