package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests the read-only WorldView contract.
 */
class WorldViewTest {
    @Test
    void worldCanBeAccessedThroughWorldView() {
        World world = new World(42L, new FlatWorldGenerator());
        IWorldView view;
        view = world;

        ChunkPosition position = new ChunkPosition(0, 0, 0);

        // read-only access
        assertNull(view.getChunk(position));

        // mutation happens on World
        world.loadChunk(position);

        // read-only view reflects mutation
        assertNotNull(view.getChunk(position));
    }
}