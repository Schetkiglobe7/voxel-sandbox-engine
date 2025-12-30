package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.chunk.LocalVoxelPosition;
import com.voxelsandbox.engine.world.coordinate.ChunkCoordinateMapper;
import com.voxelsandbox.engine.world.event.IWorldEventListener;
import com.voxelsandbox.engine.world.eviction.IChunkEvictionPolicy;
import com.voxelsandbox.engine.world.generation.IWorldGenerator;
import com.voxelsandbox.engine.world.type.VoxelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;


/**
 * Represents a voxel world composed of chunks.
 *
 * <p>
 *     The world acts as the context for a {@link IWorldGenerator} strategy,
 *     delegating chunk generation while managing chunk lifecycle.
 * </p>
 *
 * <p>
 *     Event listeners are invoked concurrently. Implementations of
 *     {@link IWorldEventListener} must therefore be thread-safe.
 * </p>
 */
public final class World implements IWorldView {
    private final long seed;
    private final IWorldGenerator generator;
    private final WorldState state = new WorldState();
    private final List<IWorldEventListener> listeners = new ArrayList<>();
    public static final int MIN_Y = 0;
    public static final int MAX_Y = 256;

    /**
     * Empty world
     */
    public World(long seed, IWorldGenerator generator) {
        this.seed = seed;
        this.generator = Objects.requireNonNull(generator, "IWorldGenerator must not be null");
    }

    /**
     * Returns an unmodifiable view of the loaded chunks.
     *
     * @return map of chunk positions to chunks.
     */
    public Map<ChunkPosition, Chunk> getChunks() {
        return this.state.getChunks();
    }

    /**
     * Returns the loaded chunk at the given position, or {@code null}
     * if the chunk is not currently present.
     *
     * <p>
     *     This method never triggers chunk generation.
     * </p>
     *
     * @param position the chunk position
     * @return the chunk, or {@code null} if not present
     */
    public Chunk getChunkIfPresent(ChunkPosition position) {
        return state.getChunkIfPresent(position);
    }

    /**
     * Loads a chunk at the given position.
     * <p>
     *     If the chunk is not already present, it is generated using the
     *     configured world generator and registered in the world state.
     * </p>
     *
     * @param position the chunk position
     * @return the loaded or newly generated chunk
     */
    public Chunk loadChunk(ChunkPosition position) {
        Objects.requireNonNull(position, "ChunkPosition must be not null");

        Chunk existingChunk = this.state.getChunkIfPresent(position);

        if (existingChunk != null) {
            notifyChunkLoaded(existingChunk);
            return existingChunk;
        }

        Chunk generatedChunk = this.generator.generateChunk(this.seed, position);
        state.putChunk(generatedChunk);

        notifyChunkGenerated(generatedChunk);
        notifyChunkLoaded(generatedChunk);

        return generatedChunk;
    }

    /**
     * Returns the seed used to initialize this world.
     *
     * <p>
     *     The seed is a deterministic value that influences world generation.
     *     Given the same seed and the same world generator strategy,
     *     the generated world content will be reproducible.
     * </p>
     *
     * @return the world seed
     */
    public long getSeed() {
        return this.seed;
    }

    /**
     * Returns the voxel type at the given world voxel coordinates.
     *
     * <p>
     *     This method provides read-only access to the voxel data exposed by the world.
     *     World coordinates are expressed in voxel space and are internally mapped
     *     to the corresponding chunk and local voxel position.
     * </p>
     *
     * <p>
     *     If the coordinates fall outside the vertical world bounds,
     *     {@link VoxelType#AIR} is returned.
     * </p>
     *
     * <p>
     *     If the chunk containing the requested voxel is not currently loaded,
     *     this method returns {@link VoxelType#AIR} without triggering chunk
     *     generation or loading.
     * </p>
     *
     * <p>
     *     This method never mutates world state and is safe to call from rendering,
     *     simulation or query systems.
     * </p>
     *
     * @param worldX world x coordinate (voxel space)
     * @param worldY world y coordinate (voxel space)
     * @param worldZ world z coordinate (voxel space)
     * @return the voxel type at the given position, or {#code AIR} if not present
     */
    @Override
    public VoxelType getVoxel(int worldX, int worldY, int worldZ) {
        if(worldY < MIN_Y || worldY > MAX_Y) {
            return VoxelType.AIR;
        }
        ChunkPosition chunkPos =
                ChunkCoordinateMapper.toChunkPosition(worldX, worldY, worldZ);
        Chunk chunk = state.getChunkIfPresent(chunkPos);
        if (chunk == null) {
            return VoxelType.AIR;
        }
        LocalVoxelPosition localPos =
                ChunkCoordinateMapper.toLocalVoxelPosition(worldX, worldY, worldZ);
        return chunk.getVoxel(localPos);
    }

    /**
     * Sets the voxel type at the given world voxel coordinates.
     *
     * <p>
     *     This method mutates world state. If the chunk containing the target voxel
     *     is not currently loaded, it is generated and registered before the
     *     modification is applied.
     * </p>
     *
     * <p>
     *     Writing outside vertical world bounds is considered a programming error
     *     and results in an exception.
     * </p>
     *
     * @param worldX world x coordinate (voxel space)
     * @param worldY world y coordinate (voxel space)
     * @param worldZ world z coordinate (voxel space)
     * @param type the type to set
     *
     * @throws IndexOutOfBoundsException if {@code worldY} is outside world bounds
     */
    public void setVoxel(int worldX, int worldY, int worldZ, VoxelType type) {
        Objects.requireNonNull(type, "VoxelType must be not null");

        if (worldY < World.MIN_Y || worldY >= World.MAX_Y) {
            throw new IndexOutOfBoundsException(
                    "Y coordinate out of world bounds: " + worldY
            );
        }

        ChunkPosition chunkPos =
                ChunkCoordinateMapper.toChunkPosition(worldX, worldY, worldZ);
        Chunk chunk = loadChunk(chunkPos);

        LocalVoxelPosition localPos =
                ChunkCoordinateMapper.toLocalVoxelPosition(worldX, worldY, worldZ);

        chunk.setVoxel(localPos, type);
    }

    /**
     * Unloads the chunk at the given position if present.
     *
     * <p>
     *     This method removes the chunk from the world state without triggering
     *     generation or loading of any other chunks.
     * </p>
     *
     * <p>
     *     If the chunk was present, it is removed and a corresponding to unload event
     *     is emitted. If the chunk not present, this method is a no-op and returns {@code null}.
     * </p>
     *
     * @param position the chunk position
     * @return the removed chunk, or {@code null} if no chunk was present
     */
    public Chunk unloadChunk(ChunkPosition position) {
        Objects.requireNonNull(position, "ChunkPosition must be not null");

        Chunk removed = this.state.removeChunk(position);
        if (removed != null) {
            notifyChunkUnloaded(removed);
        }
        return removed;
    }

    /**
     * Applies a chunk eviction policy using the given focus position.
     *
     * <p>
     *     This method delegates a candidate seleciton to the provided eviction policy
     *     and unloads all selected chunks from the world state.
     * </p>
     *
     * <p>
     *     Chunk unload events are emitted for each successfully unload chunk.
     * </p>
     *
     * @param policy the eviction policy to apply.
     * @param focus the reference chunk position (e.g. player or camera)
     * @return the number of unloaded chunks
     */
    public int applyEvictionPolicy(
            IChunkEvictionPolicy policy,
            ChunkPosition focus
    ) {
        Objects.requireNonNull(policy, "ChunkEvictionPolicy must be not null");
        Objects.requireNonNull(focus, "Focus ChunkPosition must be not null");

        return (int) StreamSupport
                .stream(policy.selectEvictionCandidates(this, focus).spliterator(), false)
                .map(this::unloadChunk)
                .filter(Objects::nonNull)
                .count();
    }

    /**
     * Registers a world event listener.
     *
     * @param listener the listener to register
     */
    public void addEventListener(IWorldEventListener listener) {
        Objects.requireNonNull(listener, "IWorldEventListener must not be null");
        this.listeners.add(listener);
    }

    /**
     * Notifies all registered {@link IWorldEventListener}s that a chunk
     * has been generated.
     *
     * <p>
     *     This event is emitted exactly once for a given chunk position,
     *     immediately after the chunk has been created by the world generator
     *     and registered in the world state.
     * </p>
     *
     * <p>
     *     This method does not perform any state mutation and is only responsible for event dispatch.
     * </p>
     *
     * @param chunk the newly generated chunk
     */
    private void notifyChunkGenerated(Chunk chunk) {
        listeners.forEach(listener -> listener.onChunkGenerated(chunk.getPosition(), chunk));
    }

    /**
     * Notifies all registered {@link IWorldEventListener}s that a chunk has been loaded and is now available in the world state.
     *
     * <p>
     *     This event is emitted both when:
     * </p>
     *
     * <ul>
     *     <li> a chunk is newly generated </li>
     *     <li> a chunk already present in the world state is accessed again </li>
     * </ul>
     *
     * <p>
     *     Listeners may use this event to trigger rendering, caching,
     *     or other read-only operations.
     * </p>
     *
     * @param chunk the loaded chunks
     */
    private void notifyChunkLoaded(Chunk chunk) {
        listeners.forEach(listener -> listener.onChunkLoaded(chunk.getPosition(), chunk));
    }

    /**
     * Notifies all registered {@link IWorldEventListener}s that a chunk
     * has been unloaded and removed from the world state.
     *
     * <p>
     *     This event is emitted only if the chunk was actually present
     *     and successfully removed.
     * </p>
     *
     * @param chunk the unloaded chunk
     */
    private void notifyChunkUnloaded(Chunk chunk) {
        listeners.forEach(listener -> listener.onChunkUnloaded(chunk.getPosition(), chunk));
    }
}