package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Represents a voxel world composed of chunks.
 *
 * <p>
 *     The world acts as an aggregate root for chunk instances.
 *     Chunk loading, generation and persistence will be introduced later.
 * </p>
 */
public final class World {
    private final Map<ChunkPosition, Chunk> chunks = new HashMap<>();

    /**
     * Empty world
     */
    public World() {}

    /**
     * Returns an unmodifiable view of the loaded chunks.
     *
     * @return map of chunk positions to chunks.
     */
    public Map<ChunkPosition, Chunk> getChunks() {
        return Collections.unmodifiableMap(chunks);
    }

    /**
     * Adds a chunk to the world.
     *
     * @param chunk the chunk to add
     */
    public void addChunk(Chunk chunk) {
        Objects.requireNonNull(chunk, "Chunk must not be null");
        this.chunks.put(chunk.getPosition(), chunk);
    }

    /**
     * Retrieves a chunk by its position.
     *
     * @param position the chunk position
     * @return the chunk, or {@code null} if not present
     */
    public Chunk getChunk(ChunkPosition position) {
        return chunks.get(position);
    }
}