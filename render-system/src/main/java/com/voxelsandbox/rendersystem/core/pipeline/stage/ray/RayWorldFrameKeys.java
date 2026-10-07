package com.voxelsandbox.rendersystem.core.pipeline.stage.ray;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;

/** Frame-scoped read-only traversal inputs. */
public final class RayWorldFrameKeys {
    private RayWorldFrameKeys() {}
    public static final FrameKey<IVoxelWorldView> WORLD = FrameKey.of("ray.world");
    public static final FrameKey<Float> MAX_DISTANCE = FrameKey.of("ray.max-distance");
}
