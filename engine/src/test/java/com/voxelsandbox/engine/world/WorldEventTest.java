package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.event.IWorldEventListener;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorldEventTest {
    @Test
    void chunkGenerated_isCalledOnlyOnce() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger generatedCount = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition position, Chunk chunk) {
                generatedCount.incrementAndGet();
            }
            @Override
            public void onChunkLoaded(ChunkPosition position, Chunk chunk) {
                //ignored
            }
        });

        ChunkPosition pos = new ChunkPosition(0, 0, 0);

        world.loadChunk(pos);

        world.loadChunk(pos);

        assertEquals(1, generatedCount.get(),
                "Chunk must be generated exactly once");
    }

    @Test
    void chunkLoaded_isCalledEveryTimeChunkIsLoaded() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger loadedCount = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition position, Chunk chunk) {
                // ignored
            }

            @Override
            public void onChunkLoaded(ChunkPosition position, Chunk chunk) {
                loadedCount.incrementAndGet();
            }
        });

        ChunkPosition pos = new ChunkPosition(0, 0, 0);

        world.loadChunk(pos);
        world.loadChunk(pos);
        world.loadChunk(pos);

        assertEquals(3, loadedCount.get(),
                "ChunkLoaded must be called on every load");
    }

    @Test
    void chunkGenerated_isAlwaysEmittedBeforeChunkLoaded() {
        World world = new World(42L, new FlatWorldGenerator());

        StringBuilder sequence = new StringBuilder();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition position, Chunk chunk) {
                sequence.append("G");
            }

            @Override
            public void onChunkLoaded(ChunkPosition position, Chunk chunk) {
                sequence.append("L");
            }
        });

        ChunkPosition pos = new ChunkPosition(0, 0, 0);

        world.loadChunk(pos);

        assertEquals("GL", sequence.toString(),
                "Chunk must be generated before being loaded");
    }

    @Test
    void getVoxel_doesNotGenerateOrLoadChunk() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger loadedCount = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition position, Chunk chunk) {
                generatedCount.incrementAndGet();
            }

            @Override
            public void onChunkLoaded(ChunkPosition position, Chunk chunk) {
                loadedCount.incrementAndGet();
            }
        });

        world.getVoxel(0, 0, 0);

        assertEquals(0, generatedCount.get(),
                "getVoxel must not generate chunks");
        assertEquals(0, loadedCount.get(),
                "getVoxel must not load chunks");
    }
}
