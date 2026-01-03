package com.voxelsandox.rendersystem.camera;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.camera.PerspectiveCamera3D;
import com.voxelsandbox.rendersystem.core.cpu.frame.CpuRenderFrame;
import com.voxelsandbox.rendersystem.core.cpu.pipeline.CpuRenderPipeline;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.math.CpuVec3f;
import com.voxelsandbox.rendersystem.core.pipeline.IRenderPipeline;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CpuCameraStage;
import com.voxelsandox.rendersystem.core.pipeline.camera.TestCameraBootstrapStage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CpuCameraStageTest {

    @Test
    void cameraStageFailsIfCameraIsMissing() {

        RenderFrame frame = new CpuRenderFrame();

        IRenderPipeline pipeline =
                new CpuRenderPipeline(List.of(
                        new CpuCameraStage()
                ));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> pipeline.execute(frame)
        );

        assertTrue(
                ex.getMessage().contains("missing required input"),
                "Error should indicate missing camera input"
        );
    }

    @Test
    void cameraStageFailsIfExecutedBeforeBootstrapStage() {

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

        IRenderPipeline pipeline =
                new CpuRenderPipeline(List.of(
                        new CpuCameraStage(),              // ❌ wrong order
                        new TestCameraBootstrapStage(camera)
                ));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> pipeline.execute(frame)
        );

        assertTrue(
                ex.getMessage().contains("missing required input"),
                "Pipeline must detect invalid stage ordering"
        );
    }
}
