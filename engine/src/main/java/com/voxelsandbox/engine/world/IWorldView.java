package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;

import java.util.Map;


/**
 * Read-only view of a voxel world.
 *
 * <p>
 *     This interface exposes safe, immutable access to world data.
 *     Implementations must not allow mutation of the underlying world state.
 * </p>
 */
public interface IWorldView {
    /**
     * Returns the chunk at the given position, or {@code null}
     * if the chunk is not loaded.
     *
     * @param position the chunk position
     * @return the chunk, or {@code null} if not present
     */
    Chunk getChunk(ChunkPosition position);

    /**
     * Returns an immutable view of the loaded chunks.
     *
     * @return map of chunk position to chunks.
     */
    Map<ChunkPosition, Chunk> getChunks();
}