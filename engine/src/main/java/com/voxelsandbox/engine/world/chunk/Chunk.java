package com.voxelsandbox.engine.world.chunk;

import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.chunk.config.ChunkDimensions;
import com.voxelsandbox.engine.world.type.VoxelType;
import com.voxelsandbox.engine.world.chunk.config.IVoxelConsumer;

import java.util.Objects;
import java.util.Arrays;


/**
 * Represents a Chunk of the Voxel world.
 * <p>
 *     A chunk is a logical container identified by its position in chunk space.
 *     Storage, voxel data, and behavior will be introduced incrementally.
 * </p>
 */
public final class Chunk {
    private final ChunkPosition position;
    private final VoxelType[] voxels;

    public Chunk(ChunkPosition position) {
        this.position = Objects.requireNonNull(position, "ChunkPosition must not be null");
        this.voxels = new VoxelType[ChunkDimensions.SIZE_X * ChunkDimensions.SIZE_Y * ChunkDimensions.SIZE_Z];
        Arrays.fill(this.voxels, VoxelType.AIR);
    }

    public ChunkPosition getPosition() {
        return this.position;
    }

    /**
     * Returns the voxel type at the given local position.
     *
     * @param pos local voxel position inside the chunk
     * @return the voxel at the specified position
     * @throws IndexOutOfBoundsException if the coordinates are outside chunk bounds
     */
    public VoxelType getVoxel(LocalVoxelPosition pos) {
        Objects.requireNonNull(pos, "LocalVoxelPosition must not be null");
        return voxels[index(pos)];
    }

    /**
     * Sets the voxel type at the given local position.
     *
     * @param pos local position inside the chunk
     * @param type the voxel type to set
     * @throws IndexOutOfBoundsException if the coordinates are outside chunk bounds
     * @throws NullPointerException if the voxel type is null
     */
    public void setVoxel(LocalVoxelPosition pos, VoxelType type) {
        Objects.requireNonNull(pos, "LocalVoxelPosition must not be null");
        Objects.requireNonNull(type, "VoxelType must not be null");
        voxels[index(pos)] = type;
    }

    /**
     * Iterates over all voxels in this chunk, providing local coordinates
     * and voxel type to the given consumer.
     *
     * @param consumer the consumer invoked for each voxel
     */
    public void forEachVoxel(IVoxelConsumer consumer) {
        Objects.requireNonNull(consumer, "IVoxelConsumer must not be null");

        for(int y = 0; y < ChunkDimensions.SIZE_Y; y++) {
            for (int z = 0; z < ChunkDimensions.SIZE_Z; z++) {
                for (int x = 0; x < ChunkDimensions.SIZE_X; x++) {
                    LocalVoxelPosition pos = new LocalVoxelPosition(x, y, z);
                    consumer.accept(pos, getVoxel(pos));
                }
            }
        }
    }

    /**
     * Computes the linear array index for the given local voxel position.
     *
     * <p>
     *     The mapping flattens 3D chunk-local coordinates into a 1D array
     *     using the following layout:
     *     <ul>
     *         <li> x is the fastest-changing axis </li>
     *         <li> z is the intermediate axis </li>
     *         <li> y is the slowest-changing axis </li>
     *     </ul>
     * </p>
     * <p>
     *     This method assumes that the provided coordinates are valid and within
     *     check bounds. Coordinate validation is handled separately.
     * </p>
     *
     * @param position the local voxel position inside the chunk
     * @return the corresponding linear index in the voxel array
     */
    private int index(LocalVoxelPosition position) {
        return position.x()
                + position.z() * ChunkDimensions.SIZE_X
                + position.y() * ChunkDimensions.SIZE_X * ChunkDimensions.SIZE_Z;
    }

    /**
     * Validates that the given local voxel position is within the bounds
     * of this chunk.
     *
     * <p>
     *     This method enforces chunk invariants by ensuring that all accesses
     *     to the internal voxel storage use valid local coordinates.
     *     Any violation is considered a programming error and results in
     *     an immediate exception.
     * </p>
     *
     * @param position the local voxel position to validate
     * @throws IndexOutOfBoundsException if any coordinate is outside chunk bounds
     */
    private void validateCoordinates(LocalVoxelPosition position) {
        if (position.x() < 0 || position.x() >= ChunkDimensions.SIZE_X) {
            throw new IndexOutOfBoundsException("x out of bounds: " + position.x());
        }
        if (position.y() < 0 || position.y() >= ChunkDimensions.SIZE_Y) {
            throw new IndexOutOfBoundsException("y out of bounds: " + position.y());
        }
        if (position.z() < 0 || position.z() >= ChunkDimensions.SIZE_Z) {
            throw new IndexOutOfBoundsException("z out of bounds: " + position.z());
        }
    }
}