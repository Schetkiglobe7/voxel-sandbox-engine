package com.voxelsandbox.rendersystem.core.raycast;


import com.voxelsandbox.rendersystem.core.math.Ray3f;
import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;

import java.util.Optional;

/**
 * Performs voxel traversal along a ray in a discrete voxel space.
 *
 * <p>
 *     Implementations traverse voxels intersected by a ray in
 *     strict parametric order using a Digital Differential Analyzer
 *     (DDA) or equivalent algorithm.
 * </p>
 *
 * <p>
 *     Traversal characteristics:
 * </p>
 * <ul>
 *     <li> deterministic </li>
 *     <li> CPU-side </li>
 *     <li> allocation-free during traversal (implementation dependent) </li>
 * </ul>
 *
 * <p>
 *     This interface is agnostic of:
 * </p>
 * <ul>
 *     <li> voxel storage format </li>
 * <li> chunk size or layout </li>
 *     <li> world bounds (finite or infinite worlds) </li>
 * </ul>
 *
 * <p>
 *     World boundaries and chunk availability are handled via
 *     {@link IVoxelWorldView}.
 * </p>
 */
public interface IVoxelRayTraversal {


    /**
     * Traverses voxels intersected by the given ray.
     *
     * <p>
     *     Voxels are visited in increasing ray-parameter order,
     *     starting from the voxel containing the ray origin.
     * </p>
     *
     * <p>
     *     Traversal terminates when:
     * </p>
     * <ul>
     *     <li> {@code maxDistance} is exceeded </li>
     *     <li> the {@link VoxelVisitor} requests early termination </li>
     *     <li> a solid voxel is detected by {@link VoxelHitPredicate} </li>
     *     <li> the voxel lies outside loaded world regions </li>
     * </ul>
     *
     * @param ray input ray (origin and normalized direction)
     * @param maxDistance maximum traversal distance along the ray (must be &gt; 0)
     * @param worldView world view used to determine chunk availability
     * @param hitPredicate predicate defining solid or hittable voxels
     * @param visitor callback invoked for each visited voxel
     *
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code maxDistance} is not positive
     */
    void traverse(
            Ray3f ray,
            float maxDistance,
            IVoxelWorldView worldView,
            VoxelHitPredicate hitPredicate,
            VoxelVisitor visitor
    );

    /**
     * Traces the ray and returns the first voxel hit.
     *
     * <p>
     *     Traversal proceeds identically to {@link #traverse},
     *     but stops immediately when a voxel satisfying the
     *     {@link VoxelHitPredicate} is encountered.
     * </p>
     *
     * <p>
     *     The returned hit result contains:
     * </p>
     * <ul>
     *     <li> voxel coordinates </li>
     *     <li> parametric hit distance </li>
     *     <li> surface normal corresponding to the entered face </li>
     * </ul>
     *
     * @param ray input ray (origin and normalized direction)
     * @param maxDistance maximum traversal distance
     * @param worldView world view used to determine chunk availability
     * @param hitPredicate predicate defining solid or hittable voxels
     *
     * @return an {@link Optional} containing the hit result,
     *         or {@code Optional.empty()} if no hit occurs
     *
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code maxDistance} is not positive
     */
    Optional<VoxelHitResult> traceFirstHit(
            Ray3f ray,
            float maxDistance,
            IVoxelWorldView worldView,
            VoxelHitPredicate hitPredicate
    );
}
