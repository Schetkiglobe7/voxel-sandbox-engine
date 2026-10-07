package com.voxelsandbox.rendersystem.demo;

import com.voxelsandbox.engine.world.IWorldView;
import com.voxelsandbox.rendersystem.adapter.EngineVoxelWorldAdapter;
import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.cpu.frame.CpuRenderFrame;
import com.voxelsandbox.rendersystem.core.cpu.pipeline.CpuRenderPipeline;
import com.voxelsandbox.rendersystem.core.pipeline.ray.CpuRayBatchingStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CpuCameraStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.cpu.CpuRayGenerationStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.output.CpuPixelOutputStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.ray.CpuRayBatchTraversalStage;
import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import java.util.List;

/** Retains the same read-only world between renders; frame data is fresh on every render. */
public final class CpuWorldRenderer {
    private final EngineVoxelWorldAdapter world;
    public CpuWorldRenderer(IWorldView world) { this.world = new EngineVoxelWorldAdapter(world); }
    public CpuRenderTarget render(ICamera3D camera,int width,int height) {
        var frame = new CpuRenderFrame();
        new CpuRenderPipeline(List.of(new CpuSceneStage(camera,world,width,height,120),
                new CpuCameraStage(),new CpuRayGenerationStage(),new CpuRayBatchingStage(),
                new CpuRayBatchTraversalStage(),new CpuPixelOutputStage())).execute(frame);
        return frame.get(CpuPixelOutputStage.TARGET).orElseThrow();
    }
}
