package com.voxelsandbox.rendersystem.core.raycast;


/**
 * Predicate used to determine whether a voxel should be considered
 * a ray hit target.
 *
 * <p>
 *     This abstraction decouples voxel traversal logic from
 *     world representation and material semantics.
 * </p>
 *
 * <p>
 *     Typical uses include:
 * </p>
 * <ul>
 *     <li> solid voxel detection </li>
 *     <li> transparency checks </li>
 *     <li> debug / visualization modes </li>
 * </ul>
 */
@FunctionalInterface
public interface VoxelHitPredicate {

    /**
     * Evaluates whether the given voxel should stop ray traversal.
     *
     * @param voxelX voxel coordinate along X axis
     * @param voxelY voxel coordinate along Y axis
     * @param voxelZ voxel coordinate along Z axis
     *
     * @return {@code true} if this voxel is considered a hit,
     *         {@code false} to continue traversal
     */
    boolean isHit(int voxelX, int voxelY, int voxelZ);
}