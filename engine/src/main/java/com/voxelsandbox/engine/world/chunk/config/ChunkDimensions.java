package com.voxelsandbox.engine.world.chunk.config;


/**
 * Defines the dimensions of a chunk in voxel units.
 *
 * <p>
 *     This class centralizes all chunk size related constants
 *     to avoid magic numbers scattered across the codebase.
 * </p>
 */
public final class ChunkDimensions {
    /**
     * Number of voxels along the X axis.
     */
    public static final int SIZE_X = 16;

    /**
     * Number of voxels along the Y axis.
     */
    public static final int SIZE_Y = 16;

    /**
     * Number of voxels along the Z axis.
     */
    public static final int SIZE_Z = 16;

    private ChunkDimensions() {
        // utility class
    }
}