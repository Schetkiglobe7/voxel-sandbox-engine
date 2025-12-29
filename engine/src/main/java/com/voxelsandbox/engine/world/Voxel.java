package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.type.VoxelType;

/**
 * Represents a single voxel in the world.
 *
 * <p>
 * A voxel is an immutable value object identified by its grid coordinates
 * and its logical type.
 * </p>
 *
 * <strong>N.B:</strong> Intentionally not a record: Voxel may evolve with behavior and state
 */
public final class Voxel {

    private final int x;
    private final int y;
    private final int z;
    private final VoxelType type;

    /**
     * Construct a voxel at the given grid coordinates.
     *
     * @param x The x-coordinate within the world grid
     * @param y The y-coordinate within the world grid
     * @param z The z-coordinate within the world grid
     */
    public Voxel(int x, int y, int z, VoxelType type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public VoxelType getType() {
        return this.type;
    }

    /**
     * Creates a new voxel {@link Voxel} instance with the same spatial coordinates
     * but different type.
     *
     * <p>
     *     This method preserves immutability by returning a new voxel instance
     *     instead of modifying the current one.
     * </p>
     *
     * @param newType the new {@link VoxelType} to associate with this voxel
     * @return a new voxel instance with the same coordinates and the given type
     */
    public Voxel withType(VoxelType newType) {
        return new Voxel(this.x, this.y, this.z, newType);
    }
}