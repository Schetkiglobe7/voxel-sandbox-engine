package com.voxelsandbox.rendersystem.core.raycast;


/**
 * Executes a single Digital Differential Analyzer (DDA) step
 * during voxel ray traversal.
 *
 * <p>
 *     This class advances a {@link VoxelRayTraversalState} by exactly
 *     one voxel, selecting the axis whose next voxel boundary is reached
 *     first according to the current {@code tMax} values.
 * </p>
 *
 * <p>
 *     The stepping logic follows the standard voxel DDA algorithm:
 * </p>
 * <ul>
 *     <li> compare {@code tMaxX}, {@code tMaxY}, {@code tMaxZ} </li>
 *     <li> advance along the axis with the smallest value </li>
 *     <li> increment the corresponding {@code tMax} by {@code tDelta} </li>
 * </ul>
 *
 * <p>
 *     This class is:
 * </p>
 * <ul>
 *     <li> deterministic </li>
 *     <li> allocation-free </li>
 *     <li> CPU-only </li>
 * </ul>
 *
 * <p>
 *     It contains no knowledge of voxel data or world bounds.
 *     It strictly operates on traversal state.
 * </p>
 */
public final class VoxelRayStepper {

    private VoxelRayStepper() {}

    /**
     * Advances the given traversal state by one DDA step.
     *
     * <p>
     *     After invocation:
     * </p>
     * <ul>
     *     <li> exactly one voxel coordinate is updated </li>
     *     <li> exactly one {@code tMax} value is increased </li>
     * </ul>
     *
     * <p>
     *     This method does not perform any bounds checking and does not
     *     terminate traversal.
     * </p>
     *
     * @param state mutable voxel ray traversal state
     *
     * @throws NullPointerException if {@code state} is {@code null}
     */
    public static void step(VoxelRayTraversalState state) {

        if (state.tMaxX < state.tMaxY) {
            if (state.tMaxX < state.tMaxZ) {
                stepX(state);
            } else {
                stepZ(state);
            }
        } else {
            if (state.tMaxY < state.tMaxZ) {
                stepY(state);
            } else {
                stepZ(state);
            }
        }
    }

    /* ==========================================================
     * Axis-specific steps
     * ========================================================== */

    /**
     * Advances the traversal state along the X axis.
     *
     * <p>
     *     Increments the current voxel X coordinate by {@code stepX}
     *     and updates {@code tMaxX} to the next boundary.
     * </p>
     */
    private static void stepX(VoxelRayTraversalState s) {
        s.voxelX += s.stepX;
        s.tMaxX  += s.tDeltaX;
    }


    /**
     * Advances the traversal state along the Y axis.
     *
     * <p>
     *     Increments the current voxel Y coordinate by {@code stepY}
     *     and updates {@code tMaxY} to the next boundary.
     * </p>
     */
    private static void stepY(VoxelRayTraversalState s) {
        s.voxelY += s.stepY;
        s.tMaxY  += s.tDeltaY;
    }

    /**
     * Advances the traversal state along the Z axis.
     *
     * <p>
     *     Increments the current voxel Z coordinate by {@code stepZ}
     *     and updates {@code tMaxZ} to the next boundary.
     * </p>
     */
    private static void stepZ(VoxelRayTraversalState s) {
        s.voxelZ += s.stepZ;
        s.tMaxZ  += s.tDeltaZ;
    }
}
