package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.generation.IWorldGenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Represents a voxel world composed of chunks.
 *
 * <p>
 *     The world acts as the context for a {@link IWorldGenerator} strategy,
 *     delegating chunk generation while managing chunk lifecycle.
 * </p>
 */
public final class World {
    private final long seed;
    private final IWorldGenerator generator;
    private final Map<ChunkPosition, Chunk> chunks = new HashMap<>();

    /**
     * Empty world
     */
    public World(long seed, IWorldGenerator generator) {
        this.seed = seed;
        this.generator = Objects.requireNonNull(generator, " IWorldGenerator must not be null");
    }

    /**
     * Returns an unmodifiable view of the loaded chunks.
     *
     * @return map of chunk positions to chunks.
     */
    public Map<ChunkPosition, Chunk> getChunks() {
        return Collections.unmodifiableMap(chunks);
    }

    /**
     * Returns the loaded chunk at the given position, or {@code null}
     * if the chunk is not currently present.
     *
     * @param position the chunk position
     * @return the chunk, or {@code null} if not present
     */
    public Chunk getChunk(ChunkPosition position) {
        return chunks.get(position);
    }

    /**
     * Loads a chunk at the given position.
     * If the chunk is not present, it is generated and registered.
     */
    public Chunk loadChunk(ChunkPosition position) {
        Objects.requireNonNull(position, "ChunkPosition must be not null");
        return chunks.computeIfAbsent(position, pos -> generator.generateChunk(seed, pos));
    }

    /**
     * Returns the seed used to initialize this world.
     *
     * <p>
     *     The seed is a deterministic value that influences world generation.
     *     Gien the same seed and the same world generator strategy,
     *     the generated world content will be reproducible.
     * </p>
     *
     * @return the world seed
     */
    public long getSeed() {
        return this.seed;
    }
}