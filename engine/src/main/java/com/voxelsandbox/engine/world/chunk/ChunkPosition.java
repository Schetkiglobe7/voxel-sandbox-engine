package com.voxelsandbox.engine.world.chunk;


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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ChunkPosition)) return false;
        ChunkPosition that = (ChunkPosition) object;
        return this.x == that.x && this.y == that.y && this.z == that.z;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(x);
        result = 31 * result + Integer.hashCode(y);
        result = 31 * result + Integer.hashCode(z);
        return result;
    }

    @java.lang.Override
    public String toString() {
        return "ChunkPosition{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}