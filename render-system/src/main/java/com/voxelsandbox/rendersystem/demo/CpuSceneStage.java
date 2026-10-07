package com.voxelsandbox.rendersystem.demo;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.frame.*;
import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.*;
import com.voxelsandbox.rendersystem.core.pipeline.stage.ray.RayWorldFrameKeys;
import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;
import java.util.*;

/** Immutable scene inputs published within an explicit frame stage. */
public record CpuSceneStage(ICamera3D camera, IVoxelWorldView world, int width, int height,
                            float maxDistance) implements IRenderStage {
    public CpuSceneStage {
        Objects.requireNonNull(camera);
        Objects.requireNonNull(world);
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Viewport must be positive");
        Math.multiplyExact(width, height);
        if (!Float.isFinite(maxDistance) || maxDistance <= 0) throw new IllegalArgumentException("Invalid distance");
    }
    @Override public String getId() { return "scene"; }
    @Override public Set<FrameKey<?>> getRequiredInputs() { return Set.of(); }
    @Override public Set<FrameKey<?>> getProducedOutputs() {
        return Set.of(CameraFrameKeys.CAMERA, CameraViewportFrameKeys.VIEWPORT_WIDTH,
                CameraViewportFrameKeys.VIEWPORT_HEIGHT, RayWorldFrameKeys.WORLD, RayWorldFrameKeys.MAX_DISTANCE);
    }
    @Override public void execute(RenderFrame frame) {
        frame.put(CameraFrameKeys.CAMERA,camera);
        frame.put(CameraViewportFrameKeys.VIEWPORT_WIDTH,width);
        frame.put(CameraViewportFrameKeys.VIEWPORT_HEIGHT,height);
        frame.put(RayWorldFrameKeys.WORLD,world);
        frame.put(RayWorldFrameKeys.MAX_DISTANCE,maxDistance);
    }
}
