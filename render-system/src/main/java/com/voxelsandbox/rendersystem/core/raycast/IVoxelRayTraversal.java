package com.voxelsandbox.rendersystem.core.raycast;


import com.voxelsandbox.rendersystem.core.math.Ray3f;

/**
 * Performs voxel traversal along a ray.
 *
 * <p>
 *     Implementations traverse a discrete voxel grid using a ray
 *     and visit voxels in the exact order intersected by the ray,
 *     typically using a Digital Differential Analyzer (DDA) algorithm.
 * </p>
 *
 * <p>
 *     Traversal is:
 * </p>
 * <ul>
 *     <li> deterministic </li>
 *     <li> CPU-side </li>
 *     <li> allocation-free (implementation dependent) </li>
 * </ul>
 *
 * <p>
 *     This interface defines no assumptions about the underlying
 *     voxel storage or coordinate system.
 * </p>
 */
public interface IVoxelRayTraversal {


    /**
     * Traverses voxels intersected by the given ray.
     *
     * <p>
     *     Voxels are visited in increasing ray parameter order
     *     starting from the ray origin and proceeding forward
     *     until {@code maxDistance} is exceeded or traversal is
     *     explicitly terminated by the {@link VoxelVisitor}.
     * </p>
     *
     * @param ray input ray (origin and normalized direction)
     * @param maxDistance maximum traversal distance along the ray (must be &gt; 0)
     * @param visitor callback invoked for each visited voxel
     *
     * @throws NullPointerException if {@code ray} or {@code visitor} is {@code null}
     * @throws IllegalArgumentException if {@code maxDistance} is not positive
     */
    void traverse(Ray3f ray, float maxDistance, VoxelVisitor visitor);
}
