package com.voxelsandbox.rendersystem.core.world;


/**
 * Read-only view over a voxel world.
 *
 * <p>
 *     This interface exposes minimal information required
 *     for rendering and ray traversal.
 * </p>
 *
 * <p>
 *     Implementations may be backed by:
 * </p>
 * <ul>
 *     <li> dense voxel arrays </li>
 *     <li> chunked worlds </li>
 *     <li> sparse structures </li>
 *     <li> procedural generators </li>
 * </ul>
 */
public interface IVoxelWorldView {

    /**
     * Returns whether the voxel at the given coordinates is solid.
     *
     * @param x voxel X coordinate
     * @param y voxel Y coordinate
     * @param z voxel Z coordinate
     *
     * @return {@code true} if the voxel is solid
     */
    boolean isSolid(int x, int y, int z);

    /**
     * Returns whether the chunk containing the given voxel
     * coordinates is currently loaded.
     *
     * @param voxelX voxel X coordinate
     * @param voxelY voxel Y coordinate
     * @param voxelZ voxel Z coordinate
     *
     * @return true if the corresponding chunk is loaded
     */
    boolean isChunkLoaded(int voxelX, int voxelY, int voxelZ);
}
