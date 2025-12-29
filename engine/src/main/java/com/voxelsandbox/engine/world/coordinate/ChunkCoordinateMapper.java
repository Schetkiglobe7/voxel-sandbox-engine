package com.voxelsandbox.engine.world.coordinate;


import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.chunk.LocalVoxelPosition;
import com.voxelsandbox.engine.world.chunk.config.ChunkDimensions;

/**
 * Utility class responsible for mapping world-space voxel coordinates
 * to chunk-space coordinates and local chunk coordinates.
 *
 * <p>
 *     This class provides a single source of truth for all coordinate
 *     transformations between:
 * </p>
 *     <ul>
 *         <li> World voxel coordinates </li>
 *         <li> Chunk grid coordinates </li>
 *         <li> Local voxel coordinates inside a chunk </li>
 *     </ul>
 * <p>
 *     All computations correctly support negative coordinates by relying on
 *     {@link Math#floorDiv(int, int)} and {@link Math#floorMod(int, int)}.
 * </p>
 *
 * <p>
 *     This class is stateless, deterministic and thread-safe.
 * </p>
 */
public final class ChunkCoordinateMapper {
    /**
     * Private constructor to prevent instantiation.
     */
    private ChunkCoordinateMapper() {}

    /**
     * Computes the chunk X coordinate corresponding to a world voxel X coordinate.
     *
     * <p>
     *     For a given world coordinate, this method returns the index of the chunk
     *     containing that voxel along a single axis.
     * </p>
     *
     * <p>
     *     This method correctly handles negative coordinates.
     * </p>
     *
     * @param worldX the world-space voxel coordinate
     * @return the chunk coordinate along the same axis.
     */
    public static int chunkX(int worldX) {
        return Math.floorDiv(worldX, ChunkDimensions.SIZE_X);
    }

    /**
     * Computes the chunk Y coordinate corresponding to a world voxel Y coordinate.
     *
     * <p>
     *     For a given world coordinate, this method returns the index of the chunk
     *     containing that voxel along a single axis.
     * </p>
     *
     * <p>
     *     This method correctly handles negative coordinates.
     * </p>
     *
     * @param worldY the world-space voxel coordinate
     * @return the chunk coordinate along the same axis.
     */
    public static int chunkY(int worldY) {
        return Math.floorDiv(worldY, ChunkDimensions.SIZE_Y);
    }

    /**
     * Computes the chunk Z coordinate corresponding to a world voxel Z coordinate.
     *
     * <p>
     *     For a given world coordinate, this method returns the index of the chunk
     *     containing that voxel along a single axis.
     * </p>
     *
     * <p>
     *     This method correctly handles negative coordinates.
     * </p>
     *
     * @param worldZ the world-space voxel coordinate
     * @return the chunk coordinate along the same axis.
     */
    public static int chunkZ(int worldZ) {
        return Math.floorDiv(worldZ, ChunkDimensions.SIZE_Z);
    }

    /**
     * Computes the local X voxel coordinate inside a chunk for a world voxel X coordinate.
     *
     * <p>
     *     The returned value is always in the range
     *     {@code 0 <= localX < ChunkDimensions.SIZE_X}.
     * </p>
     *
     * <p>
     *     This method correctly handles negative coordinates.
     * </p>
     *
     * @param worldX the world-space voxel X coordinate
     * @return the local X voxel coordinate inside the chunk
     */
    public static int localX(int worldX) {
        return Math.floorMod(worldX, ChunkDimensions.SIZE_X);
    }

    /**
     * Computes the local Y voxel coordinate inside a chunk for a world voxel Y coordinate.
     *
     * <p>
     *     The returned value is always in the range
     *     {@code 0 <= localY < ChunkDimensions.SIZE_Y}.
     * </p>
     *
     * <p>
     *     This method correctly handles negative coordinates.
     * </p>
     *
     * @param worldY the world-space voxel Y coordinate
     * @return the local Y voxel coordinate inside the chunk
     */
    public static int localY(int worldY) {
        return Math.floorMod(worldY, ChunkDimensions.SIZE_Y);
    }

    /**
     * Computes the local Z voxel coordinate inside a chunk for a world voxel Z coordinate.
     *
     * <p>
     *     The returned value is always in the range
     *     {@code 0 <= localZ < ChunkDimensions.SIZE_Z}.
     * </p>
     *
     * <p>
     *     This method correctly handles negative coordinates.
     * </p>
     *
     * @param worldZ the world-space voxel Z coordinate
     * @return the local Z voxel coordinate inside the chunk
     */
    public static int localZ(int worldZ) {
        return Math.floorMod(worldZ, ChunkDimensions.SIZE_Z);
    }

    /**
     * Converts world-space voxel coordinates into a {@link com.voxelsandbox.engine.world.chunk.ChunkPosition}.
     *
     * <p>
     *     Each component of the returned {@code ChunkPosition} represents the
     *     chunk index along the corresponding axis.
     * </p>
     *
     * @param worldX the world-space voxel X coordinate
     * @param worldY the world-space voxel y coordinate
     * @param worldZ the world-space voxel z coordinate
     * @return the chunk position containing the given voxel
     */
    public static ChunkPosition toChunkPosition(int worldX, int worldY, int worldZ) {
        return new ChunkPosition(
                chunkX(worldX),
                chunkY(worldY),
                chunkZ(worldZ)
        );
    }

    /**
     * Converts world-space voxel coordinates into local chunk coordinates.
     *
     * @param worldX world-space voxel X coordinate
     * @param worldY world-space voxel Y coordinate
     * @param worldZ world-space voxel Z coordinate
     * @return local voxel coordinates inside the chunk
     */
    public static LocalVoxelPosition toLocalVoxelPosition(int worldX, int worldY, int worldZ) {
        return new LocalVoxelPosition(
                localX(worldX),
                localY(worldY),
                localZ(worldZ)
        );
    }
}
