package com.voxelsandbox.rendersystem.core.pipeline.stage.ray;

import com.voxelsandbox.rendersystem.core.math.Vec3f;


/**
 * Result of traversing a single ray through the voxel world.
 *
 * <p>
 *     This object represents the outcome of a ray traversal,
 *     whether a hit occurred or not.
 * </p>
 *
 * <p>
 *     Instances are immutable and frame-scoped.
 * </p>
 */
public final class RayTraversalResult {

    private final boolean hit;
    private final Vec3f hitPosition;
    private final Vec3f hitNormal;
    private final float distance;

    private RayTraversalResult(
            boolean hit,
            Vec3f hitPosition,
            Vec3f hitNormal,
            float distance
    ) {
        this.hit = hit;
        this.hitPosition = hitPosition;
        this.hitNormal = hitNormal;
        this.distance = distance;
    }

    public static RayTraversalResult miss() {
        return new RayTraversalResult(false, null, null, Float.POSITIVE_INFINITY);
    }

    public static RayTraversalResult hit(
            Vec3f hitPosition,
            Vec3f hitNormal,
            float distance
    ) {
        return new RayTraversalResult(true, hitPosition, hitNormal, distance);
    }

    public boolean isHit() {
        return hit;
    }

    public Vec3f hitPosition() {
        return hitPosition;
    }

    public Vec3f hitNormal() {
        return hitNormal;
    }

    public float distance() {
        return distance;
    }
}
