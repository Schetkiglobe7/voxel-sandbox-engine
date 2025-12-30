package com.voxelsandbox.engine.world.eviction;

import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;


/**
 * Strategy interface for hunk eviction policies.
 *
 * <p>
 *     Implementations decide which chunk should be unloaded
 *     based on a focus position (e.g. player or camera).
 * </p>
 *
 * <p>
 *     Implementations MUST NOT:
 * </p>
 *
 * <ul>
 *     <li> Generate or load new chunks </li>
 *     <li> Mutate world state except via {@link World#unloadChunk(ChunkPosition)} </li>
 * </ul>
 *
 * <p>
 *     This interface is intentionally minimal to allow different
 *     eviction strategies (distance-based, memory-based, time-based).
 * </p>
 */
public interface IChunkEvictionPolicy {
    /**
     * Applies the eviction policy using the given focus position.
     *
     * <p>
     *     The focus position typically represents the player or camera
     *     location in chunk-space.
     * </p>
     *
     * @param world the world distance
     * @param focus the focus chunk position
     */
    void evict(World world, ChunkPosition focus);

    /**
     * Returns the chunk positions that are condidates for eviction.
     *
     * <p>
     *     This method MUST NOT mutate world state.
     * </p>
     *
     * @param world the world view
     * @param focus the reference chunk position (e.g. player or camera)
     * @return iterable of chunk position to evict
     */

    Iterable<ChunkPosition> selectEvictionCandidates(World world, ChunkPosition focus);
}
