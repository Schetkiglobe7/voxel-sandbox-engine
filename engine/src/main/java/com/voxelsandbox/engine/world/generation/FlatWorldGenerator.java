package com.voxelsandbox.engine.world.generation;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.type.VoxelType;


/**
 * Simple flat world generator.
 *
 * <p>
 *     Generates a flat layer of solid voxels at a fixed height,
 *     using the world seed to allow deterministic variation
 *     across different worlds.
 * </p>
 */
public final class FlatWorldGenerator implements IWorldGenerator {
    /**
     * Base height of the flat world, relative to chunk coordinates.
     */
    private static final int BASE_HEIGHT = 0;

    /**
     * Generates a chunk at the given position using a flat world strategy.
     *
     * <p>
     *     Chunks at or below the configured base height are filled entirely
     *     with solid voxels, while chunks above the remain empty (air).
     * </p>
     *
     * <p>
     *     The generation is deterministic with respect to the provided seed
     *     and chunk position.
     * </p>
     *
     * @param seed the world seed
     * @param position the position of the chunk in chunk space
     * @return the generated chunk
     */
    @Override
    public Chunk generateChunk(long seed, ChunkPosition position) {
        Chunk chunk = new Chunk(position);
        int chunkBaseY = position.y();
        if(chunkBaseY <= BASE_HEIGHT) fillChunkSolid(chunk);
        return chunk;
    }

    /**
     * Fills the entire chunk volume with solid voxels.
     *
     * @param chunk the chunk to fill.
     */
    private void fillChunkSolid(Chunk chunk) {
        chunk.forEachVoxel((position, type) -> chunk.setVoxel(position, VoxelType.SOLID));
    }
}