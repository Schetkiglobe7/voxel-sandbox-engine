package com.voxelsandbox.engine.world.chunk;

import com.voxelsandbox.engine.world.chunk.ChunkPosition;
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
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int DEPTH = 16;

    private final ChunkPosition position;
    private final VoxelType[] voxels;

    public Chunk(ChunkPosition position) {
        this.position = Objects.requireNonNull(position, "ChunkPosition must not be null");
        this.voxels = new VoxelType[WIDTH * HEIGHT * DEPTH];
        Arrays.fill(this.voxels, VoxelType.AIR);
    }

    public ChunkPosition getPosition() {
        return this.position;
    }

    /**
     * Returns the voxel type at the given local chunk coordinates.
     *
     * @param x local x coordinate
     * @param y local y coordinate
     * @param z local z coordinate
     * @return the voxel at the specified position
     * @throws IndexOutOfBoundsException if the coordinates are outside chunk bounds
     */
    public VoxelType getVoxel(int x, int y, int z) {
        validateCoordinates(x, y, z);
        return voxels[index(x, y, z)];
    }

    /**
     * Sets the voxel type at the given local chunk coordinates.
     *
     * @param x local x coordinate
     * @param y local y coordinate
     * @param z local z coordinate
     * @param type the voxel type to set
     * @throws IndexOutOfBoundsException if the coordinates are outside chunk bounds
     * @throws NullPointerException if the voxel type is null
     */
    public void setVoxel(int x, int y, int z, VoxelType type) {
        validateCoordinates(x, y, z);
        voxels[index(x, y, z)] = Objects.requireNonNull(type, "VoxelType must not be null");
    }

    /**
     * Iterates over all voxels in this chunk, providing local coordinates
     * and voxel type to the given consumer.
     *
     * @param consumer the consumer invoked for each voxel
     */
    public void forEachVoxel(IVoxelConsumer consumer) {
        Objects.requireNonNull(consumer, "VoxelConsumer must not be null");

        for(int y = 0; y < HEIGHT; y++) {
            for (int z = 0; z < DEPTH; z++) {
                for (int x = 0; x < WIDTH; x++) {
                    consumer.accept(x, y, z, getVoxel(x, y, z));
                }
            }
        }
    }

    /**
     * Computes the linear array index for the given local voxel coordinates.
     *
     * <p>
     *     The mapping flattens 3D chunk-local coordinates (x, y, z) into a 1D array
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
     * @param x local x coordinate (0 ≤ x &alt; WIDTH)
     * @param y local y coordinate (0 ≤ y &alt; WEIGHT)
     * @param z local z coordinate (0 ≤ z &alt; DEPTH)
     * @return the corresponding linear index in the voxel array
     */
    private int index(int x, int y, int z) {
        return x
                + z * WIDTH
                + y * WIDTH * DEPTH;
    }

    /**
     * Validates that the given local voxel coordinates are within the bounds
     * of this chunk.
     *
     * <p>
     *     This method enforces the chunk invariants by ensuring that all accesses
     *     to the internal voxel storage are performed using valid local coordinates.
     *     Any violation is considered a programming error and results in an immediate
     *     exception.
     * </p>
     *
     * @param x local x coordinate (0 ≤ x &alt; WIDTH)
     * @param y local y coordinate (0 ≤ y &alt; HEIGHT)
     * @param z local < coordinate (0 ≤ z &alt; DEPTH)
     */
    private void validateCoordinates(int x, int y, int z) {
        if (x < 0 || x >= WIDTH) {
            throw new IndexOutOfBoundsException("x out of bounds: " + x);
        }
        if (y < 0 || y >= HEIGHT) {
            throw new IndexOutOfBoundsException("y out of bounds: " + y);
        }
        if (z < 0 || z >= DEPTH) {
            throw new IndexOutOfBoundsException("z out of bounds: " + z);
        }
    }
}