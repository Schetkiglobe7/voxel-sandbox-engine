package com.voxelsandox.rendersystem.ray;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.camera.PerspectiveCamera3D;
import com.voxelsandbox.rendersystem.core.cpu.frame.CpuRenderFrame;
import com.voxelsandbox.rendersystem.core.cpu.pipeline.CpuRenderPipeline;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.math.CpuVec3f;
import com.voxelsandbox.rendersystem.core.math.Vec3f;
import com.voxelsandbox.rendersystem.core.pipeline.IRenderPipeline;
import com.voxelsandbox.rendersystem.core.pipeline.ray.CpuRayBatchingStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CpuCameraStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.CameraRayFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.cpu.CpuRayGenerationStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.ray.RayTraversalResult;
import com.voxelsandox.rendersystem.core.pipeline.camera.TestCameraBootstrapStage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RayBatchTraversalContractTest {

    @Test
    void traversalProducesOneResultPerRay() {

        RenderFrame frame = new CpuRenderFrame();

        ICamera3D camera = new PerspectiveCamera3D(
                new CpuVec3f(0, 0, 0),
                new CpuVec3f(0, 0, -1),
                new CpuVec3f(0, 1, 0),
                (float) Math.toRadians(60),
                1.0f,
                0.1f,
                100f
        );


        // pipeline completa fino ai batch
        IRenderPipeline pipeline = new CpuRenderPipeline(List.of(
                new TestCameraBootstrapStage(camera),
                new CpuCameraStage(),
                new CpuRayGenerationStage(),
                new CpuRayBatchingStage(),
                new DummyRayBatchTraversalStage() // stub
        ));

        pipeline.execute(frame);

        List<Vec3f> origins =
                frame.get(CameraRayFrameKeys.RAY_ORIGINS).orElseThrow();

        List<RayTraversalResult> results =
                frame.get(CameraRayFrameKeys.RAY_RESULTS).orElseThrow();

        assertEquals(origins.size(), results.size());
    }
}
