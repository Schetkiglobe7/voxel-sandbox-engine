package com.voxelsandbox.engine.world.chunk.config;

import com.voxelsandbox.engine.world.chunk.LocalVoxelPosition;
import com.voxelsandbox.engine.world.type.VoxelType;


@FunctionalInterface
public interface IVoxelConsumer {
    void accept(LocalVoxelPosition position, VoxelType type);
}