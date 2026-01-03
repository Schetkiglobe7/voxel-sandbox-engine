package com.voxelsandox.rendersystem.cpu;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.camera.PerspectiveCamera3D;
import com.voxelsandbox.rendersystem.core.cpu.frame.CpuRenderFrame;
import com.voxelsandbox.rendersystem.core.cpu.pipeline.CpuRenderPipeline;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.math.CpuVec3f;
import com.voxelsandbox.rendersystem.core.math.Vec3f;
import com.voxelsandbox.rendersystem.core.pipeline.IRenderPipeline;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CpuCameraStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.CameraRayFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.cpu.CpuRayGenerationStage;
import com.voxelsandox.rendersystem.core.pipeline.camera.TestCameraBootstrapStage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CpuRayGenerationStageTest {

    @Test
    void generatesOneRayPerPixel() {

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

        IRenderPipeline pipeline = new CpuRenderPipeline(List.of(
                new TestCameraBootstrapStage(camera),
                new CpuCameraStage(),
                new CpuRayGenerationStage()
        ));

        pipeline.execute(frame);

        List<Vec3f> origins =
                frame.get(CameraRayFrameKeys.RAY_ORIGINS).orElseThrow();

        List<Vec3f> directions =
                frame.get(CameraRayFrameKeys.RAY_DIRECTIONS).orElseThrow();

        assertEquals(800 * 600, origins.size());
        assertEquals(800 * 600, directions.size());
    }
}
