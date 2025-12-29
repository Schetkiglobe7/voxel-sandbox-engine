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
public record ChunkPosition(int x, int y, int z) {}