package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.coordinate.ChunkCoordinateMapper;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import com.voxelsandbox.engine.world.type.VoxelType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBoundsTest {
    @Test
    void getVoxel_belowMinY_returnsAir() {
        World world = new World(42L, new FlatWorldGenerator());
        IWorldView view;
        view = world;

        VoxelType voxel = view.getVoxel(0, World.MIN_Y - 1, 0);

        assertEquals(VoxelType.AIR, voxel);
    }

    @Test
    void getVoxel_aboveMaxY_returnsAir() {
        World world = new World(42L, new FlatWorldGenerator());
        IWorldView view;
        view = world;

        VoxelType voxel = view.getVoxel(0, World.MAX_Y, 0);

        assertEquals(VoxelType.AIR, voxel);
    }

    @Test
    void getVoxel_outOfBounds_doesNotLoadChunk() {
        World world = new World(42L, new FlatWorldGenerator());
        IWorldView view;
        view = world;

        ChunkPosition pos =
                ChunkCoordinateMapper.toChunkPosition(0, World.MIN_Y - 1, 0);

        assertNull(world.getChunk(pos));

        view.getVoxel(0, World.MIN_Y - 1, 0);

        assertNull(world.getChunk(pos));
    }

    @Test
    void setVoxel_belowMinY_throwsException() {
        World world = new World(42L, new FlatWorldGenerator());

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> world.setVoxel(0, World.MIN_Y - 1, 0, VoxelType.SOLID)
        );
    }

    @Test
    void setVoxel_aboveMaxY_throwsException() {
        World world = new World(42L, new FlatWorldGenerator());

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> world.setVoxel(0, World.MAX_Y, 0, VoxelType.SOLID)
        );
    }

    @Test
    void setVoxel_validCoordinates_loadsChunk() {
        World world = new World(42L, new FlatWorldGenerator());

        int y = World.MIN_Y;

        ChunkPosition pos =
                ChunkCoordinateMapper.toChunkPosition(0, y, 0);

        assertNull(world.getChunk(pos));

        world.setVoxel(0, y, 0, VoxelType.SOLID);

        assertNotNull(world.getChunk(pos));
    }
}
