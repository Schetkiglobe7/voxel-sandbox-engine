package com.voxelsandbox.engine.world.type;

/**
 * Defines the type of a voxel.
 *
 * <p>
 * This enum represents the logical identity of a voxel,
 * independent from its position or rendering details.
 * </p>
 *
 * <p>
 * The possible voxel types currently supported are:
 * </p>
 *
 * <ul>
 *   <li>{@link #AIR} – Represents the absence of a voxel.</li>
 *   <li>{@link #SOLID} – Represents the presence of a voxel.
 *       This is a placeholder for future concrete types
 *       such as grass, stone, or other materials.</li>
 * </ul>
 */
public enum VoxelType {

    AIR,
    SOLID

}