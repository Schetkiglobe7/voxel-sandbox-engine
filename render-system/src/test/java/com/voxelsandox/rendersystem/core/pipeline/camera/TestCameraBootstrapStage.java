package com.voxelsandox.rendersystem.core.pipeline.camera;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CameraFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CameraViewportFrameKeys;

import java.util.Set;


public final class TestCameraBootstrapStage implements IRenderStage {

    private final ICamera3D camera;

    public TestCameraBootstrapStage(ICamera3D camera) {
        this.camera = camera;
    }

    @Override
    public String getId() {
        return "test.camera.bootstrap";
    }

    @Override
    public Set<FrameKey<?>> getRequiredInputs() {
        return Set.of();
    }


    @Override
    public Set<FrameKey<?>> getProducedOutputs() {
        return Set.of(
                CameraFrameKeys.CAMERA,
                CameraViewportFrameKeys.VIEWPORT_WIDTH,
                CameraViewportFrameKeys.VIEWPORT_HEIGHT
        );
    }

    @Override
    public void execute(RenderFrame frame) {
        frame.put(CameraFrameKeys.CAMERA, camera);
        frame.put(CameraViewportFrameKeys.VIEWPORT_WIDTH, 800);
        frame.put(CameraViewportFrameKeys.VIEWPORT_HEIGHT, 600);
    }
}
