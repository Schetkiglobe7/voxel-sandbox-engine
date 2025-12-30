package com.voxelsandbox.engine.world.eviction;

import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;

import java.util.ArrayList;
import java.util.List;


/**
 * Chunk eviction policy based on distance from a focus position.
 *
 * <p>
 *     Chunks farther than a configurable threshold from the focus
 *     position may be unloaded.
 * </p>
 *
 * <p>
 *     This policy is intended to be used with player-based or
 *     camera-based world streaming.
 * </p>
 *
 * <p>
 *     The actual eviction decision logic is intentionally deferred
 *     to later steps.
 * </p>
 */
public class DistanceBasedChunkEvictionPolicy  implements IChunkEvictionPolicy {

    /**
     * Distance treshold (in chunk space) beyound which a chunk
     * becomes a candidate for eviction.
     *
     * <p>
     *     The distance is evaluated using the metric defined by this policy
     *     (currently Euclidean distance in chunk coordinates).
     * </p>
     *
     * <p>
     *     This value does <strong>not</strong> trigger eviction by itself:
     *     it is only used to select candidates. Actual unloading id delegated
     *     to the caller.
     * </p>
     */
    private final double evictionDistance;

    /**
     * Creates a distance-based chunk eviction policy.
     *
     * @param evictionDistance distance from focus (in chunk units
     */
    public DistanceBasedChunkEvictionPolicy(double evictionDistance) {
        this.evictionDistance = evictionDistance;
    }

    /**
     * Applies the eviction policy.
     *
     * <p>
     *     Actual eviction logic is implemented in later steps
     * </p>
     *
     * @param world the world instance
     * @param focus the focus chunk position
     */
    @Override
    public void evict(World world, ChunkPosition focus) {
        for(ChunkPosition position : world.getChunks().keySet()) {
            double distance = distance(position, focus);
        }
    }

    /**
     * Selects the chunk position that are candidated for eviction based on
     * their distance from a reference focus position.
     *
     * <p>
     *     This method performs a pure selection step:
     * </p>
     * <ul>
     *     <li> it does NOT unload chunks </li>
     *     <li> it does NOT emit world events </li>
     *     <li> it does NOT mutate world state </li>
     * </ul>
     *
     * <p>
     *     A chunk is considered a candidate if its distance from the
     *     {@code focus} position is strictly greater than {@link #evictionDistance}.
     * </p>
     *
     * <p>
     *     The distance is computed in chunk space, not voxel space.
     * </p>
     *
     * @param world a read-only view of the world state
     * @param focus the reference chunk position (e.g. player or camera chunk)
     * #return an iterable collection of chunk position eligible for eviction
     */
    @Override
    public Iterable<ChunkPosition> selectEvictionCandidates(World world, ChunkPosition focus) {
        return world.getChunks().keySet().stream()
                .filter(pos -> distance(pos, focus) > evictionDistance)
                .toList();
    }

    /**
     * Computes the distance between two chunk positions.
     * <p>
     *     Current implementation uses Euclidean distance in chunk space.
     *     This is subject to change in later steps (e.g. tanh, fuzzy metrics).
     * </p>
     */
    private double distance(ChunkPosition a, ChunkPosition b) {
        int dx = a.x() - b.x();
        int dy = a.y() - b.y();
        int dz = a.z() - b.z();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
