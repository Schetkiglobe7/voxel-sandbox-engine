package com.voxelsandox.rendersystem.ray;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.math.Vec3f;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.CameraRayFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.ray.IRayBatchTraversalStage;
import com.voxelsandbox.rendersystem.core.pipeline.stage.ray.RayTraversalResult;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Dummy implementation of a batch-aware ray traversal stage.
 *
 * <p>
 *     This stage does NOT perform any real traversal.
 *     It exists solely to validate pipeline wiring and
 *     RenderFrame contracts.
 * </p>
 *
 * <p>
 *     For each input ray, it produces a {@link RayTraversalResult#miss()}.
 * </p>
 */
public final class DummyRayBatchTraversalStage implements IRayBatchTraversalStage {

    @Override
    public String getId() {
        return "test.ray.traversal.dummy";
    }

    @Override
    public Set<FrameKey<?>> getRequiredInputs() {
        return Set.of(
                CameraRayFrameKeys.RAY_ORIGINS,
                CameraRayFrameKeys.RAY_DIRECTIONS,
                CameraRayFrameKeys.RAY_BATCHES
        );
    }

    @Override
    public Set<FrameKey<?>> getProducedOutputs() {
        return Set.of(CameraRayFrameKeys.RAY_RESULTS);
    }

    @Override
    public void execute(RenderFrame frame) {

        List<Vec3f> origins =
                frame.get(CameraRayFrameKeys.RAY_ORIGINS).orElseThrow();

        List<RayTraversalResult> results =
                IntStream.range(0, origins.size())
                        .mapToObj(i -> RayTraversalResult.miss())
                        .toList();

        frame.put(CameraRayFrameKeys.RAY_RESULTS, results);
    }
}
