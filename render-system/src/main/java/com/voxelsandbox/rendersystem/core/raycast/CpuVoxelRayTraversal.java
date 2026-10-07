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
        validate(ray, maxDistance, worldView, hitPredicate);
        VoxelRayTraversalState state = VoxelRayInitializer.initialize(ray);

        Objects.requireNonNull(visitor, "visitor");
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
                    traveled,
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

            traveled = Math.min(state.tMaxX, Math.min(state.tMaxY, state.tMaxZ));
            VoxelRayStepper.step(state);
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
        validate(ray, maxDistance, worldView, hitPredicate);
        VoxelRayTraversalState state = VoxelRayInitializer.initialize(ray);

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

                Vec3f normal = lastAxis == null ? new CpuVec3f(0, 0, 0) : switch (lastAxis) {
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

    private static void validate(Ray3f ray, float distance, IVoxelWorldView world, VoxelHitPredicate predicate) {
        Objects.requireNonNull(ray, "ray");
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(predicate, "predicate");
        if (!Float.isFinite(distance) || distance <= 0) throw new IllegalArgumentException("Distance must be finite and positive");
        Vec3f o = Objects.requireNonNull(ray.origin(), "origin");
        Vec3f d = Objects.requireNonNull(ray.direction(), "direction");
        if (!Float.isFinite(o.x()) || !Float.isFinite(o.y()) || !Float.isFinite(o.z())
                || !Float.isFinite(d.x()) || !Float.isFinite(d.y()) || !Float.isFinite(d.z())
                || Math.abs(d.lengthSquared() - 1f) > 0.0001f) {
            throw new IllegalArgumentException("Ray requires finite origin and unit direction");
        }
    }

    private enum Axis {
        X, Y, Z
    }
}

