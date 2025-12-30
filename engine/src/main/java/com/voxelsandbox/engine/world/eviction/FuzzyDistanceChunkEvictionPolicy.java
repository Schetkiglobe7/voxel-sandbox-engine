package com.voxelsandbox.engine.world.eviction;


import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;

import java.util.Objects;

/**
 * Chunk eviction policy based on a fuzzy distance metric.
 *
 * <p>
 *     This policy uses a smooth, continuous function (hyperbolic tangent)
 *     to determine whether a chunk should be considered a candidate for
 *     eviction based on its distance from a reference focus position.
 * </p>
 *
 * <p>
 *     Unlike hard-threshold policies, this implementation allows a
 *     gradual transition between "keep" and "evict" regions, reducing
 *     sudden unloading spikes during world streaming.
 * </p>
 *
 * <p>
 *     This class performs a <strong>pure selection</strong>:
 * </p>
 * <ul>
 *     <li> it does NOT unload chunks </li>
 *     <li> it does NOT emit world events </li>
 *     <li> it does NOT mutate world state </li>
 * </ul>
 */
public class FuzzyDistanceChunkEvictionPolicy implements IChunkEvictionPolicy {

    /**
     * Center distance (in chunk units) at which the fuzzy transition
     * is centered.
     */
    private final double centerDistance;

    /**
     * Controls the smoothness of the transition.
     *
     * <p>
     *     Smaller values produce a sharper transition (hard threshold-like),
     *     while larger values result in a more gradual fade-out.
     * </p>
     */
    private final double softness;

    /**
     * Threshold in the range {@code [0,1]} above which a chunk
     * is considered eligible for eviction.
     */
    private final double evictionThreshold;

    /**
     * Creates a fuzzy distance-based chunk eviction policy.
     *
     * @param centerDistance distance at which eviction probability is ~0.5
     * @param softness controls how smooth the transition is (must be > 0)
     * @param evictionThreshold cutoff value in [0,1] to select candidates
     */
    public FuzzyDistanceChunkEvictionPolicy(
            double centerDistance,
            double softness,
            double evictionThreshold
    ) {
        if (softness <= 0.0) {
            throw new IllegalArgumentException("softness must be > 0");
        }
        if (evictionThreshold < 0.0 || evictionThreshold > 1.0) {
            throw new IllegalArgumentException("evictionThreshold must be in [0,1]");
        }
        this.centerDistance = centerDistance;
        this.softness = softness;
        this.evictionThreshold = evictionThreshold;
    }

    /**
     * Selects chunk positions eligible for eviction using a fuzzy
     * distance score.
     *
     * <p>
     *     A chunk is selected if its eviction score is greater than or
     *     equal to {@link #evictionThreshold}.
     * </p>
     *
     * @param world a read-only view of the world state
     * @param focus the reference chunk position (e.g. player or camera)
     * @return iterable collection of chunk positions eligible for eviction
     */
    @Override
    public Iterable<ChunkPosition> selectEvictionCandidates(World world, ChunkPosition focus) {
        Objects.requireNonNull(world, "world must not be null");
        Objects.requireNonNull(focus, "focus must not be null");

        return world.getChunks().keySet().stream()
                .filter(pos -> evictionScore(pos, focus) >= evictionThreshold)
                .toList();
    }

    /**
     * Applies this eviction policy to the given world using the provided focus position.
     *
     * <p>
     *     This method performs the <strong>application phase</strong> of the eviction process:
     * </p>
     *
     * <ol>
     *     <li>
     *         Delegates the decision logic to {@link #selectEvictionCandidates(World, ChunkPosition)}
     *     </li>
     *     <li>
     *         Unloads each selected chunk via {@link World#unloadChunk(ChunkPosition)}
     *     </li>
     * </ol>
     *
     * <p>
     *     Chunk unload events are emitted by the {@link World} instance as a consequence
     *     of invoking {@code unloadChunk}. This method itself does not emit events directly.
     * </p>
     *
     * <p>
     *     Implementations MUST NOT:
     * </p>
     *
     * <ul>
     *     <li> generate or load new chunks </li>
     *     <li> mutate world state except via {@link World#unloadChunk(ChunkPosition)} </li>
     *     <li> apply additional filtering beyond {@code selectEvictionCandidates} </li>
     * </ul>
     *
     * <p>
     *     This method is deterministic: given the same world state and focus position,
     *     it will always unload the same set of chunks.
     * </p>
     *
     * @param world the world instance on which eviction is applied
     * @param focus the reference chunk position (e.g. player or camera)
     *
     * @throws NullPointerException if {@code world} or {@code focus} is {@code null}
     */
    @Override
    public void evict(World world, ChunkPosition focus) {
        Objects.requireNonNull(world, "world must not be null");
        Objects.requireNonNull(focus, "focus must not be null");

        for (ChunkPosition position : selectEvictionCandidates(world, focus)) {
            world.unloadChunk(position);
        }
    }

    /**
     * Computes the fuzzy eviction score for a chunk position.
     *
     * <p>
     *     The score is computed using a hyperbolic tangent function:
     * </p>
     *
     * <pre>
     * score = 0.5 * (tanh((d - center) / softness) + 1)
     * </pre>
     *
     * <p>
     *     Resulting values are in the range {@code [0,1]}.
     * </p>
     */
    private double evictionScore(ChunkPosition a, ChunkPosition b) {
        double d = distance(a, b);
        return 0.5 * (Math.tanh((d - centerDistance) / softness) + 1.0);
    }

    /**
     * Computes Euclidean distance between two chunk positions
     * in chunk space.
     */
    private double distance(ChunkPosition a, ChunkPosition b) {
        int dx = a.x() - b.x();
        int dy = a.y() - b.y();
        int dz = a.z() - b.z();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
