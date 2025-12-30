package com.voxelsandbox.engine.world.streaming;

import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;


/**
 * High-level controller responsible for chunk streaming decisions.
 *
 * <p>
 *     A {@code IChunkStreamingController} observes a reference position
 *     (e.g. player of camera) and decides which chunks should be
 *     loaded or unloaded in the world.
 * </p>
 *
 * <p>
 *     This interface defines <strong>when</strong> streaming actions
 *     should occur, but not <strong>how</strong> chunks are generated,
 *     rendered, or stored.
 * </p>
 *
 * <p>
 *     Implementations must be deterministic and must not rely on
 *     rendering or input subsystems.
 * </p>
 */
public interface IChunkStreamingController {
    /**
     * Updates the streaming state using the given focus position.
     *
     * <p>
     *     Typical responsibilities of an implementation may include:
     * </p>
     *
     * <ul>
     *     <li> loading missing chunks around the focus position </li>
     *     <li> applying eviction policies for distant chunks </li>
     * </ul>
     *
     * <p>
     *     This method is expected to be called repeatedly (e.g. once per tick
     *     or frame), but must remain safe to call even if the focus position
     *     does not change.
     * </p>
     *
     * @param world the world instance to operate on
     * @param focus the reference chunk position (e.g. player or camera)
     */
    void update(World world, ChunkPosition focus);
}
