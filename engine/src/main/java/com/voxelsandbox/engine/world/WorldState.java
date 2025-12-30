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
     * Return whether a chunk is already present in the world state.
     *
     * <p>
     *     This method is intentionally provided to support a
     *     <em>tell-don't-ask</em> interaction style between {@link World}
     *     and its internal state.
     * </p>
     *
     * <p>
     *     Typical use cases include:
     * </p>
     *
     * <ul>
     *     <li> Detecting whether a chunk will be newly generated </li>
     *     <li> Emitting lifecycle events (e.g. generates vs loaded) </li>
     *     <li> Implementing lazy-loading policies </li>
     * </ul>
     *
     * @param position the chunk position
     * @return {@code true} if a chunk is already present at the given position,
     *         {@code false} otherwise
     */
    boolean isChunkPresent(ChunkPosition position) {
        return chunks.containsKey(position);
    }

    /**
     * Rwmoves and returns the chunk at the given position if present.
     *
     * <p>
     *     This method performa a pure state mutation:
     *     no events are emitted and no generation is triggered.
     * </p>
     *
     * @param position the chunk position
     * @return the removed chunk, or {@code null} if not present
     */
    Chunk removeChunk(ChunkPosition position) {
        Objects.requireNonNull(position, "ChunkPosition must be not null");
        return chunks.remove(position);
    }
}