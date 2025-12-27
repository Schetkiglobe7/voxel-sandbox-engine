package com.voxelsandbox.engine.world.chunk;

import java.util.Objects;


/**
 * Represents the logical position of a chunk in the world.
 *
 * <p>
 *     A chunk position identifies a chunk by its discrete coordinates
 *     in chunk space, not in voxel space.
 * </p>
 *
 * <p>
 *     This class is an immutable value object and can safely be used
 *     as a key in maps or caches.
 * </p>
 */
public final class ChunkPosition {

    private final int x;
    private final int y;
    private final int z;

    public ChunkPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkPosition)) return false;
        ChunkPosition that = (ChunkPosition) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ChunkPosition{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}