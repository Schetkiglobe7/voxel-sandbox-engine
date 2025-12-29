package com.voxelsandbox.engine.world.generation;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;


/**
 * Defines a generator capable of producing chunks for a voxel world.
 *<p>
 *     Implementations must be deterministic with respect to their configuration
 *     (e.g. seed) must not retain references to generated chunks.
 *</p>
 */
public interface IWorldGenerator {
    /**
     * Generates a chunk at the given chunk position using the provided world seed.
     *
     * <p>
     *     Given the same seed and chunk position, implementations must always
     *     produce the same chunk.
     * </p>
     *
     * @param seed the world seed
     * @param position the position of the chunk in chunk space
     * @return a newly generated chunk for the given position
     */
    Chunk generateChunk(long seed, ChunkPosition position);
}