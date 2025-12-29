package com.voxelsandbox.engine.world.event;


import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;

/**
 * Listener for world lifecycle events.
 *
 * <p>
 *     Implementations may react to chunk lifecycle changes such as
 *     generation and loading.
 * </p>
 *
 * <p>
 *     Implementations MUST NOT mutate world state.
 * </p>
 */
public interface IWorldEventListener {
    /**
     * Called when a chunk is generated for the first time.
     *
     * @param position the chunk position
     * @param chunk the generated chunk
     */
    default void onChunkGenerated(ChunkPosition position, Chunk chunk) {}
    /**
     * Called when a chunk becomes available iin the world state.
     *
     * <p>
     *     This is invoked both for newly generated chunks and for chunks that where already present.
     * </p>
     *
     * @param position the chunk position
     * @param chunk the loaded chunk
     */
    default void onChunkLoaded(ChunkPosition position, Chunk chunk) {}
}
