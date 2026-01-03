package com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.cpu;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.math.Ray3f;
import com.voxelsandbox.rendersystem.core.math.Vec3f;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CameraViewportFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.IRayGenerationStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CameraFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.CameraRayFrameKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * CPU reference implementation of {@link IRayGenerationStage}.
 *
 * <p>
 * This stage generates a single world-space ray per frame,
 * corresponding to the center of the viewport.
 * </p>
 *
 * <p>
 * This implementation is intended for:
 * </p>
 * <ul>
 *     <li> validating pipeline contracts </li>
 *     <li> debugging camera → ray math </li>
 *     <li> serving as a reference for GPU parity </li>
 * </ul>
 *
 * <p>
 * The generated ray is deterministic and allocation-free
 * (except for the output container).
 * </p>
 */
public final class CpuRayGenerationStage implements IRayGenerationStage {

    private static final String ID = "cpu-ray-generation";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Set<FrameKey<?>> getRequiredInputs() {
        return Set.of(
                CameraFrameKeys.CAMERA,
                CameraViewportFrameKeys.VIEWPORT_WIDTH,
                CameraViewportFrameKeys.VIEWPORT_HEIGHT
        );
    }

    @Override
    public Set<FrameKey<?>> getProducedOutputs() {
        return Set.of(
                CameraRayFrameKeys.RAY_ORIGINS,
                CameraRayFrameKeys.RAY_DIRECTIONS
        );
    }

    @Override
    public void execute(RenderFrame frame) {

        ICamera3D camera = frame.get(CameraFrameKeys.CAMERA)
                .orElseThrow(() -> new IllegalStateException("Camera missing"));

        int width = frame.get(CameraViewportFrameKeys.VIEWPORT_WIDTH)
                .orElseThrow(() -> new IllegalStateException("Viewport width missing"));

        int height = frame.get(CameraViewportFrameKeys.VIEWPORT_HEIGHT)
                .orElseThrow(() -> new IllegalStateException("Viewport height missing"));

        List<Vec3f> origins = new ArrayList<>(width * height);
        List<Vec3f> directions = new ArrayList<>(width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                float sx = (x + 0.5f);
                float sy = (y + 0.5f);

                Ray3f ray = camera.generateRay(
                        sx,
                        sy,
                        width,
                        height
                );

                origins.add(ray.origin());
                directions.add(ray.direction());
            }
        }

        frame.put(CameraRayFrameKeys.RAY_ORIGINS, origins);
        frame.put(CameraRayFrameKeys.RAY_DIRECTIONS, directions);
    }
}
