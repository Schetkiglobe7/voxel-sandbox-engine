package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.chunk.LocalVoxelPosition;
import com.voxelsandbox.engine.world.chunk.config.ChunkDimensions;
import com.voxelsandbox.engine.world.coordinate.ChunkCoordinateMapper;
import com.voxelsandbox.engine.world.generation.IWorldGenerator;
import com.voxelsandbox.engine.world.type.VoxelType;

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
public final class World implements IWorldView {
    private final long seed;
    private final IWorldGenerator generator;
    private final WorldState state = new WorldState();

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
     * @param position the chunk position
     * @return the chunk, or {@code null} if not present
     */
    public Chunk getChunk(ChunkPosition position) {
        return state.getChunk(position);
    }

    /**
     * Loads a chunk at the given position.
     * <p>
     *     If the chunk is not present, it is generated and registered.
     *     Otherwise, it is generated, registered and returned.
     * </p>
     *
     * @param position the chunk position
     * @return the loaded or newly generated chunk
     */
    public Chunk loadChunk(ChunkPosition position) {
        Objects.requireNonNull(position, "ChunkPosition must be not null");
        return this.state.ensureChunkPresent(position, this.seed, this.generator);
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
        Chunk chunk = state.getChunk(chunkPos);
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
}