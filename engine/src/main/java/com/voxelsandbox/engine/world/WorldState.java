package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.generation.IWorldGenerator;

import java.util.Collections;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;


/**
 * Internal mutable state of a World.
 *
 * <p>
 *     This class is responsible for storing and managing loaded chunks.
 *     It is not intended to be accessed directly outside the world package
 * </p>
 */
final class WorldState {
    private final Map<ChunkPosition, Chunk> chunks = new HashMap<>();

    /**
     * Returns ad unmodifiable view of the loaded chunks.
     *
     * @return map of chunk positions to chunks
     */
    Map<ChunkPosition, Chunk> getChunks() {
        return Collections.unmodifiableMap(chunks);
    }

    /**
     * Returns the chunk at the given position if it is already loaded.
     *
     * <p>
     *     This method never generates or loads new chunks.
     * </p>
     *
     * @param position the chunk position.
     * @return the chunk if present, or {@code null} otherwise
     */
    Chunk getChunkIfPresent(ChunkPosition position) {
        return chunks.get(position);
    }

    /**
     * Adds a chunks to the state.
     *
     * @param chunk the chunk to add
     */
    void putChunk(Chunk chunk) {
        Objects.requireNonNull(chunk, "Chunk must be not null");
        chunks.put(chunk.getPosition(), chunk);
    }

    /**
     * Ensures that a chunk exists at the given position.
     *
     * <p>
     *     If a chunk is already present in the world state at the given position,
     *     that instance is returned. Otherwise, a new chunk is generated using
     *     the provided world generator and seed, registered internally, and returned.
     * </p>
     *
     * @param position the chunk position
     * @param generator the chunk generator strategy
     * @param seed the world seed used for deterministic generation
     * @return the existing chunk if already present, or the newly generated chunk.
     */
    Chunk ensureChunkPresent(
            ChunkPosition position,
            long seed,
            IWorldGenerator generator
    ) {
        return chunks.computeIfAbsent(position, pos -> generator.generateChunk(seed, pos));
    }
}