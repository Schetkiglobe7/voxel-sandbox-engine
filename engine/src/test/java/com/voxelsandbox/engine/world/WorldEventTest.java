package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.event.IWorldEventListener;
import com.voxelsandbox.engine.world.eviction.DistanceBasedChunkEvictionPolicy;
import com.voxelsandbox.engine.world.eviction.IChunkEvictionPolicy;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import com.voxelsandbox.engine.world.streaming.DistanceBasedChunkStreamingController;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void chunkUnloaded_isCalledOnceWhenChunkIsRemoved() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger unloadedCount = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkUnloaded(ChunkPosition position, Chunk chunk) {
                unloadedCount.incrementAndGet();
            }
        });

        ChunkPosition pos = new ChunkPosition(0, 0, 0);

        world.loadChunk(pos);
        world.unloadChunk(pos);

        assertEquals(1, unloadedCount.get(),
                "ChunkUnloaded must be called exactly once");
    }

    @Test
    void unloadingMissingChunk_doesNotEmitEvent() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger unloadedCount = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkUnloaded(ChunkPosition position, Chunk chunk) {
                unloadedCount.incrementAndGet();
            }
        });

        world.unloadChunk(new ChunkPosition(0, 0, 0));

        assertEquals(0, unloadedCount.get(),
                "Unloading a missing chunk must not emit unload event");
    }

    @Test
    void unloadingSameChunkTwice_emitsEventOnlyOnce() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger unloadedCount = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkUnloaded(ChunkPosition position, Chunk chunk) {
                unloadedCount.incrementAndGet();
            }
        });

        ChunkPosition pos = new ChunkPosition(0, 0, 0);

        world.loadChunk(pos);
        world.unloadChunk(pos);
        world.unloadChunk(pos);

        assertEquals(1, unloadedCount.get(),
                "ChunkUnloaded must be emitted only once");
    }

    @Test
    void chunkLifecycle_eventsFollowCorrectOrder() {
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

            @Override
            public void onChunkUnloaded(ChunkPosition position, Chunk chunk) {
                sequence.append("U");
            }
        });

        ChunkPosition pos = new ChunkPosition(0, 0, 0);

        world.loadChunk(pos);
        world.unloadChunk(pos);

        assertEquals("GLU", sequence.toString(),
                "Chunk lifecycle must follow Generate → Load → Unload");
    }

    @Test
    void reloadingAfterUnload_doesNotRegenerateChunk() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger generated = new AtomicInteger();
        AtomicInteger loaded = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition position, Chunk chunk) {
                generated.incrementAndGet();
            }

            @Override
            public void onChunkLoaded(ChunkPosition position, Chunk chunk) {
                loaded.incrementAndGet();
            }
        });

        ChunkPosition pos = new ChunkPosition(0, 0, 0);

        world.loadChunk(pos);   // generate + load
        world.unloadChunk(pos); // unload
        world.loadChunk(pos);   // generate + load again

        assertEquals(2, loaded.get(),
                "ChunkLoaded must be called on every load");

        assertEquals(2, generated.get(),
                "Chunk must be regenerated after unload");
    }

    @Test
    void noEventsEmittedAfterUnloadWithoutReload() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger loaded = new AtomicInteger();
        AtomicInteger generated = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition position, Chunk chunk) {
                generated.incrementAndGet();
            }

            @Override
            public void onChunkLoaded(ChunkPosition position, Chunk chunk) {
                loaded.incrementAndGet();
            }
        });

        ChunkPosition pos = new ChunkPosition(0, 0, 0);

        world.loadChunk(pos);
        world.unloadChunk(pos);

        // No further calls
        assertEquals(1, generated.get());
        assertEquals(1, loaded.get());
    }

    @Test
    void eviction_unloadsOnlyDistantChunks() {
        World world = new World(42L, new FlatWorldGenerator());

        // focus (player / camera chunk)
        ChunkPosition focus = new ChunkPosition(0, 0, 0);

        // carichiamo chunk vicini (DEVONO restare)
        ChunkPosition near1 = new ChunkPosition(0, 0, 0);
        ChunkPosition near2 = new ChunkPosition(1, 0, 0);
        ChunkPosition near3 = new ChunkPosition(0, 0, 1);

        // carichiamo chunk lontani (DEVONO essere unloadati)
        ChunkPosition far1 = new ChunkPosition(5, 0, 0);
        ChunkPosition far2 = new ChunkPosition(-6, 0, 0);
        ChunkPosition far3 = new ChunkPosition(0, 0, 7);

        world.loadChunk(near1);
        world.loadChunk(near2);
        world.loadChunk(near3);
        world.loadChunk(far1);
        world.loadChunk(far2);
        world.loadChunk(far3);

        // sanity check
        assertEquals(6, world.getChunks().size(), "All chunks must be loaded before eviction");

        // eviction policy: distanza in chunk-space
        double evictionDistance = 2.0;
        IChunkEvictionPolicy policy =
                new DistanceBasedChunkEvictionPolicy(evictionDistance);

        int unloadedCount = world.applyEvictionPolicy(policy, focus);

        // 3 chunk devono essere rimossi
        assertEquals(3, unloadedCount, "Exactly 3 distant chunks must be unloaded");

        // chunk vicini DEVONO restare
        assertNotNull(world.getChunkIfPresent(near1));
        assertNotNull(world.getChunkIfPresent(near2));
        assertNotNull(world.getChunkIfPresent(near3));

        // chunk lontani DEVONO sparire
        assertNull(world.getChunkIfPresent(far1));
        assertNull(world.getChunkIfPresent(far2));
        assertNull(world.getChunkIfPresent(far3));
    }

    @Test
    void chunkUnloaded_isEmittedExactlyOnce() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger unloadedCount = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkUnloaded(ChunkPosition position, Chunk chunk) {
                unloadedCount.incrementAndGet();
            }
        });

        ChunkPosition focus = new ChunkPosition(0, 0, 0);
        ChunkPosition farChunk = new ChunkPosition(10, 0, 0);

        // load chunk explicitly
        world.loadChunk(farChunk);

        IChunkEvictionPolicy policy =
                new DistanceBasedChunkEvictionPolicy(5.0);

        // first eviction → should unload
        world.applyEvictionPolicy(policy, focus);

        // second eviction → chunk already gone
        world.applyEvictionPolicy(policy, focus);

        assertEquals(
                1,
                unloadedCount.get(),
                "onChunkUnloaded must be emitted exactly once per chunk"
        );
    }

    @Test
    void unload_doesNotEmitLoadOrGenerateEvents() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger loadedCount = new AtomicInteger();
        AtomicInteger unloadedCount = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition position, Chunk chunk) {
                generatedCount.incrementAndGet();
            }

            @Override
            public void onChunkLoaded(ChunkPosition position, Chunk chunk) {
                loadedCount.incrementAndGet();
            }

            @Override
            public void onChunkUnloaded(ChunkPosition position, Chunk chunk) {
                unloadedCount.incrementAndGet();
            }
        });

        ChunkPosition focus = new ChunkPosition(0, 0, 0);
        ChunkPosition farChunk = new ChunkPosition(10, 0, 0);

        // Load once
        world.loadChunk(farChunk);

        assertEquals(1, generatedCount.get(), "Chunk must be generated once");
        assertEquals(1, loadedCount.get(), "Chunk must be loaded once");

        IChunkEvictionPolicy policy =
                new DistanceBasedChunkEvictionPolicy(5.0);

        // Evict
        world.applyEvictionPolicy(policy, focus);

        assertEquals(
                1,
                unloadedCount.get(),
                "Chunk must be unloaded exactly once"
        );

        assertEquals(
                1,
                generatedCount.get(),
                "Unload must NOT generate chunks"
        );

        assertEquals(
                1,
                loadedCount.get(),
                "Unload must NOT load chunks"
        );
    }

    @Test
    void streamingController_loadsAllChunksWithinLoadRadius() {
        World world = new World(42L, new FlatWorldGenerator());

        int loadRadius = 1;

        DistanceBasedChunkStreamingController controller =
                new DistanceBasedChunkStreamingController(
                        loadRadius,
                        new DistanceBasedChunkEvictionPolicy(5.0)
                );

        ChunkPosition focus = new ChunkPosition(0, 0, 0);

        controller.update(world, focus);

        int expectedLoadedChunks =
                (2 * loadRadius + 1)
                        * (2 * loadRadius + 1)
                        * (2 * loadRadius + 1);

        assertEquals(
                expectedLoadedChunks,
                world.getChunks().size(),
                "All chunks within loadRadius must be loaded"
        );
    }

    @Test
    void streamingController_isIdempotent() {
        World world = new World(42L, new FlatWorldGenerator());

        AtomicInteger generated = new AtomicInteger();
        AtomicInteger unloaded = new AtomicInteger();

        world.addEventListener(new IWorldEventListener() {
            @Override
            public void onChunkGenerated(ChunkPosition position, Chunk chunk) {
                generated.incrementAndGet();
            }

            @Override
            public void onChunkUnloaded(ChunkPosition position, Chunk chunk) {
                unloaded.incrementAndGet();
            }
        });

        DistanceBasedChunkStreamingController controller =
                new DistanceBasedChunkStreamingController(
                        1,
                        new DistanceBasedChunkEvictionPolicy(5.0)
                );

        ChunkPosition focus = new ChunkPosition(0, 0, 0);

        controller.update(world, focus);
        int firstChunkCount = world.getChunks().size();
        int firstGenerated = generated.get();

        controller.update(world, focus);

        assertEquals(
                firstChunkCount,
                world.getChunks().size(),
                "Chunk count must not change on repeated update"
        );

        assertEquals(
                firstGenerated,
                generated.get(),
                "No new chunks must be generated on repeated update"
        );

        assertEquals(
                0,
                unloaded.get(),
                "No chunks must be unloaded when focus does not change"
        );
    }
}
