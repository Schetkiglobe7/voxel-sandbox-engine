package com.voxelsandbox.rendersystem.core.chunk;


/**
 * Read-only view of a voxel chunk for rendering purposes.
 *
 * <p>
 *     This interface defines the minimal contract required by the
 *     rendering system to visualize a chunk.
 * </p>
 *
 * <p>
 *     Implementations MUST be immutable and must not expose
 *     engine-internal data structures.
 * </p>
 */
public interface IRenderChunkView {

    /**
     * Chunk X coordinate in chunk space.
     *
     * @return chunk X
     */
    int getChunkX();

    /**
     * Chunk Y coordinate in chunk space.
     *
     * @return chunk Y
     */
    int getChunkY();

    /**
     * Chunk Z coordinate in chunk space.
     *
     * @return chunk Z
     */
    int getChunkZ();

    /**
     * Size of the chunk along one axis (assuming cubic chunks).
     *
     * @return chunk size in voxels
     */
    int getChunkSize();

    /**
     * Returns the voxel type at the given local coordinates.
     *
     * <p>
     *     Coordinates are expressed in local chunk space:
     *     {@code [0, ChunkSize - 1]}.
     * </p>
     *
     * <p>
     *     This method MUST NOT trigger chunk loading or generation.
     * </p>
     *
     * @param x local x coordinate
     * @param y local y coordinate
     * @param z local z coordinate
     * @return an integer voxel identifier
     */
    int getVoxel(int x, int y, int z);

    /**
     * Returns a stable identifier for this chunk.
     *
     * <p>
     *     Used by rener systems for caching, mesh reuse,
     *     and dirty tracking.
     * </p>
     *
     * @return stable chunk id
     */
    long getChunkId();

}
