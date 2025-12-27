package com.voxelsandbox.engine.world.chunk.config;

import com.voxelsandbox.engine.world.type.VoxelType;


@FunctionalInterface
public interface IVoxelConsumer {
    void accept(int x, int y, int z, VoxelType type);
}