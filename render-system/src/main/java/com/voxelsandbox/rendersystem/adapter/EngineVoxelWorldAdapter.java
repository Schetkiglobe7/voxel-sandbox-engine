package com.voxelsandbox.rendersystem.adapter;

import com.voxelsandbox.engine.world.IWorldView;
import com.voxelsandbox.engine.world.coordinate.ChunkCoordinateMapper;
import com.voxelsandbox.engine.world.type.VoxelType;
import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;
import java.util.Objects;

/** Read-only bridge; queries never generate or load chunks. */
public final class EngineVoxelWorldAdapter implements IVoxelWorldView {
    private final IWorldView world;
    public EngineVoxelWorldAdapter(IWorldView world) { this.world = Objects.requireNonNull(world); }
    @Override public boolean isSolid(int x, int y, int z) { return world.getVoxel(x, y, z) == VoxelType.SOLID; }
    @Override public boolean isChunkLoaded(int x, int y, int z) {
        return world.getChunkIfPresent(ChunkCoordinateMapper.toChunkPosition(x, y, z)) != null;
    }
}
