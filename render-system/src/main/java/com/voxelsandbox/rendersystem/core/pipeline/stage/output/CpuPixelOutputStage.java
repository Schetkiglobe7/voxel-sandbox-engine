package com.voxelsandbox.rendersystem.core.pipeline.stage.output;

import com.voxelsandbox.rendersystem.core.frame.*;
import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CameraViewportFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.CameraRayFrameKeys;
import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import java.util.Set;

/** Diagnostic face shading; creates a fresh target for each frame. */
public final class CpuPixelOutputStage implements IRenderStage {
    public static final FrameKey<CpuRenderTarget> TARGET = FrameKey.of("cpu.pixel-target");
    public static final int SKY = 0xFF87B8DB;
    @Override public String getId() { return "cpu-pixel-output"; }
    @Override public Set<FrameKey<?>> getRequiredInputs() {
        return Set.of(CameraRayFrameKeys.RAY_RESULTS, CameraViewportFrameKeys.VIEWPORT_WIDTH,
                CameraViewportFrameKeys.VIEWPORT_HEIGHT);
    }
    @Override public Set<FrameKey<?>> getProducedOutputs() { return Set.of(TARGET); }
    @Override public void execute(RenderFrame frame) {
        int width = frame.get(CameraViewportFrameKeys.VIEWPORT_WIDTH).orElseThrow();
        int height = frame.get(CameraViewportFrameKeys.VIEWPORT_HEIGHT).orElseThrow();
        var hits = frame.get(CameraRayFrameKeys.RAY_RESULTS).orElseThrow();
        if (hits.size() != Math.multiplyExact(width,height)) throw new IllegalArgumentException("Result count differs from viewport");
        var target = new CpuRenderTarget(width,height);
        target.beginFrame();
        for (int i = 0; i < hits.size(); i++) {
            var hit = hits.get(i);
            int color = SKY;
            if (hit.isHit()) {
                var normal = hit.hitNormal();
                float light = .45f + .45f * Math.max(0, normal.y()) + .10f * Math.abs(normal.x());
                var p = hit.hitPosition();
                boolean alternate = (Math.floorDiv((int)Math.floor(p.x()),4)
                        + Math.floorDiv((int)Math.floor(p.z()),4)) % 2 == 0;
                int r = alternate ? 110 : 90, g = alternate ? 170 : 145, b = alternate ? 90 : 75;
                color = 0xFF000000 | ((int)(r*light) << 16) | ((int)(g*light) << 8) | (int)(b*light);
            }
            target.drawPixel(i % width,i / width,color);
        }
        target.endFrame();
        frame.put(TARGET,target);
    }
}
