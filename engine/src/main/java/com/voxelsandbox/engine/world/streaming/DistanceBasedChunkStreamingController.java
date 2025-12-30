package com.voxelsandbox.engine.world.streaming;


import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.eviction.IChunkEvictionPolicy;

import java.util.Objects;

/**
 * Streaming controller based on distance from a focus chunk position.
 *
 * <p>
 *     This controller is responsible for keeping a cubic region of chunks
 *     around a reference {@code focus} position loaded, while unloading
 *     chunks that fall outside the desired streaming area.
 * </p>
 *
 * <p>
 *     Chunk loading is performed eagerly within a configurable
 *     {@code loadRadius}. Chunk unloading is delegated to a pluggable
 *     {@link IChunkEvictionPolicy}.
 * </p>
 *
 * <p>
 *     This controller is deterministic and idempotent:
 *     invoking {@link #update(World, ChunkPosition)} multiple times with
 *     the same parameters produces the same world state.
 * </p>
 */
public class DistanceBasedChunkStreamingController implements IChunkStreamingController {
    /**
     * Radius (in chunks units) defining the cubic region around the focus
     * position that must be kept loaded.
     */
    private final int loadRadius;
    /**
     * Eviction policy used to select unload chunks that are no longer
     * considered relevant.
     */
    private final IChunkEvictionPolicy evictionPolicy;

    /**
     * Created a distance-based chunk streaming controller.
     *
     * @param loadRadius radius (in chunk units) around the focus position
     *                   that must be kept loaded
     * @param evictionPolicy policy used to select chunks eligible for eviction
     *
     * @throws IllegalArgumentException if {@code loadRadius < 0}
     * @throws NullPointerException if {@code evictionPolicy} is {@code null}
     */
    public DistanceBasedChunkStreamingController(
            int loadRadius,
            IChunkEvictionPolicy evictionPolicy
    ) {
        if (loadRadius < 0) {
            throw new IllegalArgumentException("loadRadius must be >= 0");
        }
        this.loadRadius = loadRadius;
        this.evictionPolicy = Objects.requireNonNull(evictionPolicy, "Eviction policy must not be null");
    }

    /**
     * Updates the streamed world state around a focus chunk position.
     *
     * <p>
     *     This method performs two distinct steps:
     * </p>
     * <ol>
     *     <li>
     *         Ensure that all chunks within {@link #loadRadius} from the
     *         focus position are loaded.
     *     </li>
     *     <li>
     *         Applies the configured eviction policy to unload chunks that
     *         are no longer relevant.
     *     </li>
     * </ol>
     *
     * <p>
     *     This method may trigger chunk generation, loading and unloading
     *     events via the {@link World} instance.
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

        // 1. Ensure chunks around focus are loaded
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

        // 2. Apply evictionPolicy for distant chunks
        world.applyEvictionPolicy(evictionPolicy, focus);
    }
}
