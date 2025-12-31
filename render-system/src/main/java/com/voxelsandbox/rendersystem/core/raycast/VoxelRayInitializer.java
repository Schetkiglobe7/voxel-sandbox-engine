package com.voxelsandbox.rendersystem.core.raycast;

import com.voxelsandbox.rendersystem.core.math.Ray3f;
import com.voxelsandbox.rendersystem.core.math.Vec3f;


/**
 * Utility class responsible for initializing voxel ray traversal state
 * using the Digital Differential Analyzer (DDA) algorithm.
 *
 * <p>
 *     This class performs the mathematical conversion from a continuous
 *     world-space {@link Ray3f} into discrete voxel traversal parameters
 *     required to step through a voxel grid.
 * </p>
 *
 * <p>
 *     Responsibilities of this class include:
 * </p>
 * <ul>
 *     <li> determining the initial voxel containing the ray origin </li>
 *     <li> computing step directions along each axis </li>
 *     <li> computing parametric distances {@code tMax} and {@code tDelta} </li>
 * </ul>
 *
 * <p>
 *     This class is purely mathematical:
 * </p>
 * <ul>
 *     <li> no access to voxel data </li>
 *     <li> no traversal or stepping logic </li>
 *     <li> no side effects </li>
 * </ul>
 *
 * <p>
 *     All computations are deterministic and allocation-free
 *     (aside from the returned state object).
 * </p>
 */
public class VoxelRayInitializer {

    private VoxelRayInitializer() {}

    /**
     * Initializes a {@link VoxelRayTraversalState} for the given ray.
     *
     * <p>
     *     The resulting state can be consumed by a DDA stepper
     *     to traverse voxels intersected by the ray in correct order.
     * </p>
     *
     * <p>
     *     The ray direction is expected to be normalized.
     * </p>
     *
     * @param ray input ray in world space
     * @return initialized voxel traversal state
     *
     * @throws NullPointerException if {@code ray} is {@code null}
     */
    public static VoxelRayTraversalState initialize(Ray3f ray) {
        Vec3f origin = ray.origin();
        Vec3f dir = ray.direction();

        /* ----------------------------------------------------------
         * Initial voxel coordinates (integer grid cell)
         * ---------------------------------------------------------- */

        int voxelX = (int) Math.floor(origin.x());
        int voxelY = (int) Math.floor(origin.y());
        int voxelZ = (int) Math.floor(origin.z());

        /* ----------------------------------------------------------
         * Step direction along each axis (+1, -1 or 0)
         * ---------------------------------------------------------- */

        int stepX = sign(dir.x());
        int stepY = sign(dir.y());
        int stepZ = sign(dir.z());

        /* ----------------------------------------------------------
         * Parametric distance required to cross one voxel
         * along each axis
         * ---------------------------------------------------------- */

        float tDeltaX = delta(dir.x());
        float tDeltaY = delta(dir.y());
        float tDeltaZ = delta(dir.z());

        /* ----------------------------------------------------------
         * Distance to first voxel boundary along each axis
         * ---------------------------------------------------------- */

        float tMaxX = firstIntersection(origin.x(), dir.x(), voxelX, stepX);
        float tMaxY = firstIntersection(origin.y(), dir.y(), voxelY, stepY);
        float tMaxZ = firstIntersection(origin.z(), dir.z(), voxelZ, stepZ);

        return new VoxelRayTraversalState(
                voxelX, voxelY, voxelZ,
                stepX, stepY, stepZ,
                tMaxX, tMaxY, tMaxZ,
                tDeltaX, tDeltaY, tDeltaZ
        );
    }


    /* ==========================================================
     * Helper methods
     * ========================================================== */

    /**
     * Returns the sign of the given value as a step direction.
     *
     * @param v input value
     * @return {@code 1} if positive, {@code -1} if negative, {@code 0} if zero
     */
    private static int sign(float v) {
        if (v > 0f) return 1;
        if (v < 0f) return -1;
        return 0;
    }


    /**
     * Computes the parametric distance required to cross
     * one voxel along an axis.
     *
     * <p>
     *     For zero direction components, the distance is infinite,
     *     meaning the ray never crosses voxel boundaries along that axis.
     * </p>
     *
     * @param dir direction component
     * @return delta distance for the axis
     */
    private static float delta(float dir) {
        if (dir == 0f) return Float.POSITIVE_INFINITY;
        return Math.abs(1f / dir);
    }

    /**
     * Computes the parametric distance to the first voxel boundary
     * along a given axis.
     *
     * @param origin ray origin coordinate along the axis
     * @param dir ray direction component along the axis
     * @param voxel current voxel coordinate
     * @param step step direction along the axis
     * @return distance to first voxel boundary
     */
    private static float firstIntersection(
            float origin,
            float dir,
            int voxel,
            int step
    ) {
        if (dir == 0f) return Float.POSITIVE_INFINITY;

        float nextBoundary =
                step > 0
                        ? voxel + 1f
                        : voxel;
        return (nextBoundary - origin) / dir;
    }
}
