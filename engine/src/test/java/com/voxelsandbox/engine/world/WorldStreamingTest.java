package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.event.IWorldEventListener;
import com.voxelsandbox.engine.world.eviction.DistanceBasedChunkEvictionPolicy;
import com.voxelsandbox.engine.world.eviction.IChunkEvictionPolicy;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import com.voxelsandbox.engine.world.streaming.DistanceBasedChunkStreamingController;
import com.voxelsandbox.engine.world.streaming.IChunkStreamingController;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorldStreamingTest {

    @Test
    void movingFocus_loadsAndUnloadsCorrectChunks() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger generated = new AtomicInteger();
        AtomicInteger loaded = new AtomicInteger();
        AtomicInteger unloaded = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition pos, Chunk c) {
                generated.incrementAndGet();
            }

            @Override
            public void onChunkLoaded(ChunkPosition pos, Chunk c) {
                loaded.incrementAndGet();
            }

            @Override
            public void onChunkUnloaded(ChunkPosition pos, Chunk c) {
                unloaded.incrementAndGet();
            }
        });

        IChunkEvictionPolicy eviction =
                new DistanceBasedChunkEvictionPolicy(1.5);

        IChunkStreamingController controller =
                new DistanceBasedChunkStreamingController(1, eviction);

        // first focus
        controller.update(world, new ChunkPosition(0,0,0));

        int initialChunkCount = world.getChunks().size();

        // move focus
        controller.update(world, new ChunkPosition(1,0,0));

        assertTrue(unloaded.get() > 0,
                "Chunks leaving the radius must be unloaded");

        assertTrue(world.getChunks().size() <= initialChunkCount + 3,
                "Only a small frontier of chunks should be added");
    }

    @Test
    void repeatedUpdates_doNotLeakChunksOrEvents() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger generated = new AtomicInteger();
        AtomicInteger unloaded = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition p, Chunk c) {
                generated.incrementAndGet();
            }

            @Override
            public void onChunkUnloaded(ChunkPosition p, Chunk c) {
                unloaded.incrementAndGet();
            }
        });

        IChunkEvictionPolicy eviction =
                new DistanceBasedChunkEvictionPolicy(2.5);

        IChunkStreamingController controller =
                new DistanceBasedChunkStreamingController(1, eviction);

        ChunkPosition focus = new ChunkPosition(0,0,0);

        for (int i = 0; i < 100; i++) {
            controller.update(world, focus);
        }

        int chunkCount = world.getChunks().size();

        assertTrue(chunkCount <= 27,
                "Chunk count must stay bounded");

        assertTrue(generated.get() < 100,
                "Chunks must not be regenerated repeatedly");
    }
}
