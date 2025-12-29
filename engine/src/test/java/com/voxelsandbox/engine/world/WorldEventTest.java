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
}
