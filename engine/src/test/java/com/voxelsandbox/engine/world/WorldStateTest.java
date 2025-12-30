package com.voxelsandbox.engine.world;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.eviction.DistanceBasedChunkEvictionPolicy;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import com.voxelsandbox.engine.world.streaming.DistanceBasedChunkStreamingController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldStateTest {

    @Test
     void putChunk_makesChunkPresent() {
        WorldState state = new WorldState();
        ChunkPosition pos = new ChunkPosition(0, 0, 0);
        Chunk chunk = new Chunk(pos);

        state.putChunk(chunk);

        assertTrue(state.isChunkPresent(pos));
        assertSame(chunk, state.getChunkIfPresent(pos));
    }

    @Test
    void removeChunk_removesAndReturnsChunk() {
        WorldState state = new WorldState();
        ChunkPosition pos = new ChunkPosition(1, 2, 3);
        Chunk chunk = new Chunk(pos);

        state.putChunk(chunk);

        Chunk removed = state.removeChunk(pos);

        assertSame(chunk, removed);
        assertFalse(state.isChunkPresent(pos));
        assertNull(state.getChunkIfPresent(pos));
    }

    @Test
    void removeChunk_onMissingChunk_returnsNull() {
        WorldState state = new WorldState();
        ChunkPosition pos = new ChunkPosition(5, 5, 5);

        Chunk removed = state.removeChunk(pos);

        assertNull(removed);
        assertFalse(state.isChunkPresent(pos));
    }

    @Test
    void removeChunk_doesNotAffectOtherChunks() {
        WorldState state = new WorldState();

        ChunkPosition pos1 = new ChunkPosition(0, 0, 0);
        ChunkPosition pos2 = new ChunkPosition(1, 0, 0);

        Chunk chunk1 = new Chunk(pos1);
        Chunk chunk2 = new Chunk(pos2);

        state.putChunk(chunk1);
        state.putChunk(chunk2);

        state.removeChunk(pos1);

        assertFalse(state.isChunkPresent(pos1));
        assertTrue(state.isChunkPresent(pos2));
    }

}
