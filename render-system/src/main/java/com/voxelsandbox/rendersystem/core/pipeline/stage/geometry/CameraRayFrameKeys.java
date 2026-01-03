package com.voxelsandbox.rendersystem.core.pipeline.stage.geometry;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.math.Vec3f;
import com.voxelsandbox.rendersystem.core.pipeline.ray.RayBatch;
import com.voxelsandbox.rendersystem.core.pipeline.stage.ray.RayTraversalResult;

import java.util.List;


/**
 * Frame keys produced by ray generation stages.
 */
public final class CameraRayFrameKeys {

    private CameraRayFrameKeys() {}

    /**
     * World-space ray origins, one per generated ray.
     */
    public static final FrameKey<List<Vec3f>> RAY_ORIGINS =
            FrameKey.of("ray.origins");

    /**
     * World-space ray directions, one per generated ray.
     *
     * <p>
     *     Directions are expected to be normalized.
     * </p>
     */
    public static final FrameKey<List<Vec3f>> RAY_DIRECTIONS =
            FrameKey.of("ray.directions");

    /**
     * Ray batches describing logical groupings of generated rays.
     */
    public static final FrameKey<List<RayBatch>> RAY_BATCHES =
            FrameKey.of("ray.batches");

    /**
     * Results of ray traversal, one per generated ray.
     */
    public static final FrameKey<List<RayTraversalResult>> RAY_RESULTS =
            FrameKey.of("ray.results");
}
