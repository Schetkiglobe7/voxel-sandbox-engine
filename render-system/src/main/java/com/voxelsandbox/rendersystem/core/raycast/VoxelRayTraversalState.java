package com.voxelsandbox.rendersystem.core.raycast;


/**
 * Holds the mutable state required for voxel ray traversal
 * using the Digital Differential Analyzer (DDA) algorithm.
 *
 * <p>
 *     This class represents the <strong>entire traversal state</strong>
 *     of a ray as it progresses through a discrete voxel grid.
 * </p>
 *
 * <p>
 *     It contains only data and no traversal logic:
 * </p>
 * <ul>
 *     <li> no stepping decisions </li>
 *     <li> no world access </li>
 *     <li> no termination conditions </li>
 * </ul>
 *
 * <p>
 *     The state is intended to be:
 * </p>
 * <ul>
 *     <li> mutated incrementally by a DDA stepper </li>
 *     <li> inspected by traversal or visitor logic </li>
 *     <li> reused across different traversal strategies </li>
 * </ul>
 *
 * <p>
 *     This design enables:
 * </p>
 * <ul>
 *     <li> deterministic CPU traversal </li>
 *     <li> clear separation of concerns </li>
 *     <li> future CPU/GPU parity </li>
 * </ul>
 */
public final class VoxelRayTraversalState {

    /* ==========================================================
     * Current voxel coordinates
     * ========================================================== */

    /**
     * Current voxel X coordinate.
     */
    public int voxelX;

    /**
     * Current voxel Y coordinate.
     */
    public int voxelY;
    /**
     * Current voxel Z coordinate.
     */
    public int voxelZ;


    /* ==========================================================
     * Step direction (+1, -1 or 0)
     * ========================================================== */

    /**
     * Step direction along the X axis.
     *
     * <p>
     *     Value is typically {@code +1}, {@code -1}, or {@code 0}
     *     depending on ray direction.
     * </p>
     */
    public final int stepX;
    /**
     * Step direction along the Y axis.
     */
    public final int stepY;
    /**
     * Step direction along the Z axis.
     */
    public final int stepZ;


    /* ==========================================================
     * Parametric distances along the ray
     * ========================================================== */

    /**
     * Distance along the ray to the next voxel boundary on X.
     */
    public float tMaxX;

    /**
     * Distance along the ray to the next voxel boundary on Y.
     */
    public float tMaxY;

    /**
     * Distance along the ray to the next voxel boundary on Z.
     */
    public float tMaxZ;

    /**
     * Distance required to cross one voxel along X.
     */
    public final float tDeltaX;

    /**
     * Distance required to cross one voxel along Y.
     */
    public final float tDeltaY;

    /**
     * Distance required to cross one voxel along Z.
     */
    public final float tDeltaZ;

    /**
     * Creates a new voxel ray traversal state.
     *
     * <p>
     *     All parameters are assumed to be precomputed by a
     *     {@link VoxelRayInitializer} or equivalent setup logic.
     * </p>
     *
     * @param voxelX initial voxel X coordinate
     * @param voxelY initial voxel Y coordinate
     * @param voxelZ initial voxel Z coordinate
     * @param stepX step direction along X axis
     * @param stepY step direction along Y axis
     * @param stepZ step direction along Z axis
     * @param tMaxX initial distance to first X boundary
     * @param tMaxY initial distance to first Y boundary
     * @param tMaxZ initial distance to first Z boundary
     * @param tDeltaX distance to cross one voxel on X
     * @param tDeltaY distance to cross one voxel on Y
     * @param tDeltaZ distance to cross one voxel on Z
     */
    public VoxelRayTraversalState(
            int voxelX,
            int voxelY,
            int voxelZ,
            int stepX,
            int stepY,
            int stepZ,
            float tMaxX,
            float tMaxY,
            float tMaxZ,
            float tDeltaX,
            float tDeltaY,
            float tDeltaZ
    ) {
        this.voxelX = voxelX;
        this.voxelY = voxelY;
        this.voxelZ = voxelZ;

        this.stepX = stepX;
        this.stepY = stepY;
        this.stepZ = stepZ;

        this.tMaxX = tMaxX;
        this.tMaxY = tMaxY;
        this.tMaxZ = tMaxZ;

        this.tDeltaX = tDeltaX;
        this.tDeltaY = tDeltaY;
        this.tDeltaZ = tDeltaZ;
    }
}
