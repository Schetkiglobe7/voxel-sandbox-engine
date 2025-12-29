package com.voxelsandbox.engine.world.chunk;


/**
 * Represents the local voxel coordinates inside a chunk.
 *
 * <p>
 *     All components are guaranteed to be within chunk bounds
 *     {@code [0, SIZE_X]}, {@code [0, SIZE_Y]}, {@code [0, SIZE_Z]}.
 * </p>
 *
 * @param x local X coordinate inside the chunk (0 ≤ x &lt; SIZE_X)
 * @param y local Y coordinate inside the chunk (0 ≤ y &lt; SIZE_Y)
 * @param z local Z coordinate inside the chunk (0 ≤ z &lt; SIZE_Z)
 */
public record LocalVoxelPosition(int x, int y, int z) { }
