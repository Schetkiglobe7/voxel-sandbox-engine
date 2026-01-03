package com.voxelsandox.rendersystem.core.pipeline.camera;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.camera.PerspectiveCamera3D;
import com.voxelsandbox.rendersystem.core.cpu.frame.CpuRenderFrame;
import com.voxelsandbox.rendersystem.core.cpu.pipeline.CpuRenderPipeline;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.math.CpuVec3f;
import com.voxelsandbox.rendersystem.core.pipeline.IRenderPipeline;
import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CameraFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CpuCameraStage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test for {@link CpuCameraStage}.
 */
public class CpuCameraStageTest {

    @Test
    void cameraStageProducesViewAndProjectionMatrices() {

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

        IRenderStage bootstrap = new TestCameraBootstrapStage(camera);
        IRenderStage cameraStage = new CpuCameraStage();

        IRenderPipeline pipeline =
                new CpuRenderPipeline(List.of(bootstrap, cameraStage));

        pipeline.execute(frame);

        assertTrue(frame.contains(CameraFrameKeys.VIEW_MATRIX));
        assertTrue(frame.contains(CameraFrameKeys.PROJECTION_MATRIX));
    }

    @Test
    void pipelineExecutesCameraStageCorrectly() {

        RenderFrame frame = new CpuRenderFrame();

        ICamera3D camera = new PerspectiveCamera3D(
                new CpuVec3f(0, 0, 0),
                new CpuVec3f(0, 0, -1),
                new CpuVec3f(0, 1, 0),
                (float) Math.toRadians(70),
                16f / 9f,
                0.1f,
                500f
        );

        IRenderPipeline pipeline =
                new CpuRenderPipeline(List.of(
                        new TestCameraBootstrapStage(camera),
                        new CpuCameraStage()
                ));

        pipeline.execute(frame);

        assertTrue(frame.contains(CameraFrameKeys.VIEW_MATRIX));
        assertTrue(frame.contains(CameraFrameKeys.PROJECTION_MATRIX));
    }
}
