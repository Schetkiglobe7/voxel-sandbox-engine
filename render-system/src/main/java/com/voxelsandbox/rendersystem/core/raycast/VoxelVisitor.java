package com.voxelsandbox.rendersystem.core.raycast;

import com.voxelsandbox.rendersystem.core.math.Vec3f;


/**
 * Callback invoked for each voxel intersected during ray traversal.
 *
 * <p>
 *     This interface defines a visitor-style contract used by voxel
 *     ray traversal algorithms (such as DDA) to report the sequence
 *     of voxel cells intersected by a ray.
 * </p>
 *
 * <p>
 *     Voxels are visited in <strong>strict traversal order</strong>,
 *     corresponding to increasing parametric distance along the ray.
 * </p>
 *
 * <p>
 *     The visitor:
 * </p>
 * <ul>
 *     <li> observes traversal events </li>
 *     <li> does <strong>not</strong> influence stepping logic </li>
 *     <li> may terminate traversal early via its return value </li>
 * </ul>
 *
 * <p>
 *     Implementations MUST NOT:
 * </p>
 * <ul>
 *     <li> mutate traversal state </li>
 *     <li> assume parallel execution </li>
 *     <li> rely on side effects between visits </li>
 * </ul>
 *
 * <p>
 *     This interface is intentionally minimal to allow:
 * </p>
 * <ul>
 *     <li> deterministic CPU traversal </li>
 *     <li> easy testability </li>
 *     <li> future GPU or SIMD parity </li>
 * </ul>
 */
@FunctionalInterface
public interface VoxelVisitor {

    /**
     * Invoked when the traversal enters a voxel cell.
     *
     * <p>
     *     This method is called exactly once per visited voxel,
     *     in the order defined by the traversal algorithm.
     * </p>
     *
     * @param voxelX voxel coordinate along the X axis
     * @param voxelY voxel coordinate along the Y axis
     * @param voxelZ voxel coordinate along the Z axis
     * @param tEnter parametric distance along the ray at which
     *               the voxel boundary is entered
     * @param rayOrigin ray origin in world space
     * @param rayDirection normalized ray direction in world space
     *
     * @return {@code true} to continue traversal,
     *         {@code false} to stop traversal immediately
     */
    boolean visit(
            int voxelX,
            int voxelY,
            int voxelZ,
            float tEnter,
            Vec3f rayOrigin,
            Vec3f rayDirection
    );
}