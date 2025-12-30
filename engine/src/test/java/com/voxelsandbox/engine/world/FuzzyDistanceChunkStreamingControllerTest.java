package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.eviction.FuzzyDistanceChunkEvictionPolicy;
import com.voxelsandbox.engine.world.eviction.IChunkEvictionPolicy;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import com.voxelsandbox.engine.world.streaming.FuzzyDistanceChunkStreamingController;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuzzyDistanceChunkStreamingControllerTest {

    @Test
    void loadsAllChunksWithinLoadRadius() {
        World world = new World(42L, new FlatWorldGenerator());

        IChunkEvictionPolicy eviction =
                new FuzzyDistanceChunkEvictionPolicy(
                        6.0,    // center
                        1.5,    // softness
                        0.7     // threshold
                );

        FuzzyDistanceChunkStreamingController controller =
                new FuzzyDistanceChunkStreamingController(1, eviction);

        ChunkPosition focus = new ChunkPosition(0, 0, 0);

        controller.update(world, focus);

        // Cubo 3x3x3 = 27 chunk
        assertEquals(27, world.getChunks().size(),
                "All chunks within loadRadius must be loaded");
    }

    @Test
    void evictionPolicySelectionIsUsed() {
        World world = new World(42L, new FlatWorldGenerator());

        ChunkPosition far = new ChunkPosition(10, 0, 0);
        world.loadChunk(far);

        IChunkEvictionPolicy policy = new IChunkEvictionPolicy() {
            @Override
            public void evict(World world, ChunkPosition focus) {

            }

            @Override
            public Iterable<ChunkPosition> selectEvictionCandidates(World w, ChunkPosition f) {
                return List.of(far);
            }
        };

        FuzzyDistanceChunkStreamingController controller =
                new FuzzyDistanceChunkStreamingController(0, policy);

        controller.update(world, new ChunkPosition(0, 0, 0));

        assertFalse(world.getChunks().containsKey(far),
                "Controller must apply eviction policy selection");
    }

    @Test
    void updateIsIdempotent() {
        World world = new World(42L, new FlatWorldGenerator());

        IChunkEvictionPolicy eviction =
                new FuzzyDistanceChunkEvictionPolicy(
                        6.0,    // center
                        1.5,    // softness
                        0.7     // threshold
                );

        FuzzyDistanceChunkStreamingController controller =
                new FuzzyDistanceChunkStreamingController(1, eviction);

        ChunkPosition focus = new ChunkPosition(0, 0, 0);

        controller.update(world, focus);
        int firstSize = world.getChunks().size();

        controller.update(world, focus);
        int secondSize = world.getChunks().size();

        assertEquals(firstSize, secondSize,
                "Repeated updates with same focus must not change world state");
    }

    @Test
    void doesNotLoadChunksOutsideLoadRadius() {
        World world = new World(42L, new FlatWorldGenerator());

        IChunkEvictionPolicy eviction =
                new FuzzyDistanceChunkEvictionPolicy(
                        6.0,    // center
                        1.5,    // softness
                        0.7     // threshold
                );

        FuzzyDistanceChunkStreamingController controller =
                new FuzzyDistanceChunkStreamingController(1, eviction);

        controller.update(world, new ChunkPosition(0, 0, 0));

        assertFalse(world.getChunks().containsKey(new ChunkPosition(2, 0, 0)));
        assertFalse(world.getChunks().containsKey(new ChunkPosition(0, 2, 0)));
        assertFalse(world.getChunks().containsKey(new ChunkPosition(0, 0, 2)));
    }

    @Test
    void chunksSelectedByPolicyAreUnloaded() {
        World world = new World(42L, new FlatWorldGenerator());

        ChunkPosition far = new ChunkPosition(10, 0, 0);

        // Pre-carica chunk lontano
        world.loadChunk(far);

        // Policy che seleziona SEMPRE quel chunk
        IChunkEvictionPolicy evictAllPolicy = new IChunkEvictionPolicy() {

            @Override
            public void evict(World world, ChunkPosition focus) {
                // intentionally empty
                // eviction is handled by World.applyEvictionPolicy(...)
            }

            @Override
            public Iterable<ChunkPosition> selectEvictionCandidates(World w, ChunkPosition f) {
                return List.of(far);
            }
        };
        FuzzyDistanceChunkStreamingController controller =
                new FuzzyDistanceChunkStreamingController(0, evictAllPolicy);

        controller.update(world, new ChunkPosition(0, 0, 0));

        assertFalse(world.getChunks().containsKey(far),
                "Chunks selected by eviction policy must be unloaded");
    }
}
