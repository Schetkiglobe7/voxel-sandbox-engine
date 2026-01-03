package com.voxelsandbox.rendersystem.core.pipeline.ray;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CameraViewportFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.CameraRayFrameKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public final class CpuRayBatchingStage implements IRenderStage {

    private static final int TILE_SIZE = 16;

    @Override
    public String getId() {
        return "ray.batching";
    }

    @Override
    public Set<FrameKey<?>> getRequiredInputs() {
        return Set.of(
                CameraRayFrameKeys.RAY_ORIGINS,
                CameraRayFrameKeys.RAY_DIRECTIONS,
                CameraViewportFrameKeys.VIEWPORT_WIDTH,
                CameraViewportFrameKeys.VIEWPORT_HEIGHT
        );
    }

    @Override
    public Set<FrameKey<?>> getProducedOutputs() {
        return Set.of(CameraRayFrameKeys.RAY_BATCHES);
    }

    @Override
    public void execute(RenderFrame frame) {

        int width = frame.get(CameraViewportFrameKeys.VIEWPORT_WIDTH).orElseThrow();
        int height = frame.get(CameraViewportFrameKeys.VIEWPORT_HEIGHT).orElseThrow();

        List<RayBatch> batches = new ArrayList<>();

        int index = 0;

        for (int y = 0; y < height; y += TILE_SIZE) {
            for (int x = 0; x < width; x += TILE_SIZE) {

                int tileWidth = Math.min(TILE_SIZE, width - x);
                int tileHeight = Math.min(TILE_SIZE, height - y);

                int count = tileWidth * tileHeight;

                batches.add(new RayBatch(index, count));

                index += count;
            }
        }

        frame.put(CameraRayFrameKeys.RAY_BATCHES, batches);
    }
}
