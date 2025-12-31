package com.voxelsandbox.rendersystem.core.raycast;

import com.voxelsandbox.rendersystem.core.math.CpuVec3f;
import com.voxelsandbox.rendersystem.core.math.Ray3f;
import com.voxelsandbox.rendersystem.core.math.Vec3f;
import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;

import java.util.Objects;
import java.util.Optional;


/**
 * CPU reference implementation of voxel ray traversal based on
 * a Digital Differential Analyzer (DDA) algorithm.
 *
 * <p>
 *     This implementation traverses a discrete voxel grid along
 *     a ray, visiting voxels in strict parametric order.
 * </p>
 *
 * <p>
 *     It acts as the authoritative CPU reference for:
 * </p>
 * <ul>
 *     <li> correctness validation </li>
 *     <li> unit and integration testing </li>
 *     <li> GPU parity verification </li>
 * </ul>
 *
 * <p>
 *     Key properties:
 * </p>
 * <ul>
 *     <li> deterministic traversal order </li>
 *     <li> no allocations during traversal </li>
 *     <li> support for infinite worlds via {@link IVoxelWorldView} </li>
 *     <li> early exit on chunk boundaries, hits or visitor termination </li>
 * </ul>
 */
public final class CpuVoxelRayTraversal implements IVoxelRayTraversal {

    /**
     * {@inheritDoc}
     *
     * <p>
     *     This method performs a full voxel traversal and invokes
     *     the provided {@link VoxelVisitor} for each visited voxel.
     * </p>
     *
     * <p>
     *     Traversal terminates when:
     * </p>
     * <ul>
     *     <li> {@code maxDistance} is exceeded </li>
     *     <li> the visitor returns {@code false} </li>
     *     <li> a solid voxel is detected by {@link VoxelHitPredicate} </li>
     *     <li> the voxel lies in an unloaded chunk </li>
     * </ul>
     */
    @Override
    public void traverse(
            Ray3f ray,
            float maxDistance,
            IVoxelWorldView worldView,
            VoxelHitPredicate hitPredicate,
            VoxelVisitor visitor
    )  {
        VoxelRayTraversalState state =
                VoxelRayInitializer.initialize(ray);

        float traveled = 0f;

        while (traveled <= maxDistance) {

            if (!worldView.isChunkLoaded(
                    state.voxelX,
                    state.voxelY,
                    state.voxelZ
            )) {
                return; // stop traversal immediately
            }

            boolean shouldContinue = visitor.visit(
                    state.voxelX,
                    state.voxelY,
                    state.voxelZ,
                    Math.min(state.tMaxX,
                            Math.min(state.tMaxY, state.tMaxZ)),
                    ray.origin(),
                    ray.direction()
            );

            if (!shouldContinue) {
                return;
            }

            if (hitPredicate.isHit(
                    state.voxelX,
                    state.voxelY,
                    state.voxelZ
            )) {
                return;
            }

            float prevT =
                    Math.min(state.tMaxX,
                            Math.min(state.tMaxY, state.tMaxZ));

            VoxelRayStepper.step(state);

            float nextT =
                    Math.min(state.tMaxX,
                            Math.min(state.tMaxY, state.tMaxZ));

            traveled += Math.abs(nextT - prevT);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     This method traces the ray until the first solid voxel
     *     is encountered, returning detailed hit information.
     * </p>
     *
     * <p>
     *     The returned {@link VoxelHitResult} contains:
     * </p>
     * <ul>
     *     <li> voxel coordinates </li>
     *     <li> parametric hit distance </li>
     *     <li> outward-facing surface normal </li>
     * </ul>
     *
     * <p>
     *     Traversal terminates immediately on:
     * </p>
     * <ul>
     *     <li> first solid voxel hit </li>
     *     <li> leaving loaded world regions </li>
     *     <li> exceeding {@code maxDistance} </li>
     * </ul>
     */
    @Override
    public Optional<VoxelHitResult> traceFirstHit(
            Ray3f ray,
            float maxDistance,
            IVoxelWorldView worldView,
            VoxelHitPredicate hitPredicate
    ) {
        VoxelRayTraversalState state =
                VoxelRayInitializer.initialize(ray);

        float t = 0f;
        Axis lastAxis = null;

        while (t <= maxDistance) {

            if (!worldView.isChunkLoaded(
                    state.voxelX,
                    state.voxelY,
                    state.voxelZ
            )) {
                return Optional.empty();
            }

            if (hitPredicate.isHit(
                    state.voxelX,
                    state.voxelY,
                    state.voxelZ
            )) {

                Vec3f normal = switch (lastAxis) {
                    case X -> new CpuVec3f(-state.stepX, 0, 0);
                    case Y -> new CpuVec3f(0, -state.stepY, 0);
                    case Z -> new CpuVec3f(0, 0, -state.stepZ);
                    default -> new CpuVec3f(0, 0, 0); // starting voxel
                };

                return Optional.of(
                        new VoxelHitResult(
                                state.voxelX,
                                state.voxelY,
                                state.voxelZ,
                                t,
                                normal
                        )
                );
            }

            // Snapshot tMax BEFORE stepping
            float prevX = state.tMaxX;
            float prevY = state.tMaxY;
            float prevZ = state.tMaxZ;

            VoxelRayStepper.step(state);

            // Determine which axis advanced
            if (state.tMaxX != prevX) {
                lastAxis = Axis.X;
                t = prevX;
            } else if (state.tMaxY != prevY) {
                lastAxis = Axis.Y;
                t = prevY;
            } else {
                lastAxis = Axis.Z;
                t = prevZ;
            }
        }

        return Optional.empty();
    }

    private enum Axis {
        X, Y, Z
    }
}

