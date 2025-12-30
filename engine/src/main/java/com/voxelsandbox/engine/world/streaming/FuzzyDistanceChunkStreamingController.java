package com.voxelsandbox.engine.world.streaming;


import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.eviction.IChunkEvictionPolicy;

import java.util.Objects;

/**
 * Streaming controller based on a fuzzy distance metric.
 *
 * <p>
 *     This controller maintains a region of chunks around a reference
 *     {@code focus} position loaded, while unloading distant chunks
 *     using a fuzzy eviction policy.
 * </p>
 *
 * <p>
 *     Chunk loading is performed eagerly within a fixed {@code loadRadius}.
 *     Chunk unloading is delegated to a {@link IChunkEvictionPolicy} that
 *     applies a smooth, continuous distance function (e.g. tanh-based).
 * </p>
 *
 * <p>
 *     Unlike hard-threshold streaming, this controller allows gradual
 *     unloading behavior, reducing sudden eviction spikes when the
 *     focus position moves.
 * </p>
 *
 * <p>
 *     This controller is deterministic and idempotent:
 *     invoking {@link #update(World, ChunkPosition)} multiple times with
 *     the same parameters produces the same world state.
 * </p>
 */
public class FuzzyDistanceChunkStreamingController implements IChunkStreamingController {

    /**
     * Radius (in chunk units) defining the cubic region around the focus
     * position that must be kept loaded.
     */
    private final int loadRadius;

    /**
     * Eviction policy implementing fuzzy distance-based selection.
     */
    private final IChunkEvictionPolicy evictionPolicy;

    /**
     * Creates a fuzzy distance-based chunk streaming controller.
     *
     * @param loadRadius radius (in chunk units) around the focus position
     *                   that must be kept loaded
     * @param evictionPolicy fuzzy eviction policy used to select chunks
     *                       eligible for unloading
     *
     * @throws IllegalArgumentException if {@code loadRadius < 0}
     * @throws NullPointerException if {@code evictionPolicy} is {@code null}
     */
    public FuzzyDistanceChunkStreamingController(
            int loadRadius,
            IChunkEvictionPolicy evictionPolicy
    ) {
        if (loadRadius < 0) {
            throw new IllegalArgumentException("loadRadius must be >= 0");
        }
        this.loadRadius = loadRadius;
        this.evictionPolicy =
                Objects.requireNonNull(evictionPolicy, "Eviction policy must not be null");
    }

    /**
     * Updates the streamed world state around a focus chunk position.
     *
     * <p>
     *     This method performs two steps:
     * </p>
     *
     * <ol>
     *     <li>
     *         Ensures that all chunks within {@link #loadRadius} of the
     *         focus position are loaded.
     *     </li>
     *     <li>
     *         Applies the fuzzy eviction policy to unload chunks that
     *         are no longer considered relevant.
     *     </li>
     * </ol>
     *
     * <p>
     *     Chunk generation, loading and unloading events may be emitted
     *     by the {@link World} instance as a result of this update.
     * </p>
     *
     * @param world the world to update
     * @param focus the reference chunk position (e.g. player or camera)
     *
     * @throws NullPointerException if {@code world} or {@code focus} is {@code null}
     */
    @Override
    public void update(World world, ChunkPosition focus) {
        Objects.requireNonNull(world, "world must not be null");
        Objects.requireNonNull(focus, "focus must not be null");

        // 1. Load chunks around focus
        for (int dx = -loadRadius; dx <= loadRadius; dx++) {
            for (int dy = -loadRadius; dy <= loadRadius; dy++) {
                for (int dz = -loadRadius; dz <= loadRadius; dz++) {
                    ChunkPosition pos = new ChunkPosition(
                            focus.x() + dx,
                            focus.y() + dy,
                            focus.z() + dz
                    );
                    world.loadChunk(pos);
                }
            }
        }

        // 2. Apply fuzzy eviction
        world.applyEvictionPolicy(evictionPolicy, focus);
    }
}
