package com.voxelsandbox.rendersystem.core.raycast;

import com.voxelsandbox.rendersystem.core.math.Vec3f;


/**
 * Immutable result of a voxel ray intersection.
 *
 * <p>
 *     Represents the first voxel cell hit by a ray during traversal,
 *     including geometric and parametric information.
 * </p>
 *
 * <p>
 *     This class is purely a data carrier and contains no traversal logic.
 * </p>
 */
public final class VoxelHitResult {

    /**
     * Voxel coordinates of the hit cell.
     */
    public final int voxelX;
    public final int voxelY;
    public final int voxelZ;

    /**
     * Parametric distance along the ray where the hit occurred.
     */
    public final float t;

    /**
     * Surface normal of the voxel face that was hit.
     *
     * <p>
     *     This vector is axis-aligned and normalized.
     * </p>
     */
    public final Vec3f normal;

    public VoxelHitResult(
            int voxelX,
            int voxelY,
            int voxelZ,
            float t,
            Vec3f normal
    ) {
        this.voxelX = voxelX;
        this.voxelY = voxelY;
        this.voxelZ = voxelZ;
        this.t = t;
        this.normal = normal;
    }
}
