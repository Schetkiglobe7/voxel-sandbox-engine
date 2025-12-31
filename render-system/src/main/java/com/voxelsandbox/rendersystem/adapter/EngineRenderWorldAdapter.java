package com.voxelsandbox.rendersystem.adapter;

import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.config.ChunkDimensions;
import com.voxelsandbox.rendersystem.core.chunk.IRenderChunkView;
import com.voxelsandbox.rendersystem.core.world.IRenderWorldView;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;



/**
 * Adapter that exposes an engine {@link World} as a {@link IRenderWorldView}.
 *
 * <p>
 *     This class represents the canonical boundary between the engine core
 *     and the rendering system.
 * </p>
 *
 * <p>
 *     It provides a read-only, render-friendly view of the current world state,
 *     exposing all currently loaded engine chunks as {@link IRenderChunkView}
 *     instances.
 * </p>
 *
 * <p>
 *     This adapter is intentionally minimal and strictly read-only.
 *     It MUST NOT:
 * </p>
 * <ul>
 *     <li> mutate world state </li>
 *     <li> trigger chunk loading or generation </li>
 *     <li> apply streaming or eviction logic </li>
 * </ul>
 *
 * <p>
 *     The returned views always reflect the current state of the underlying
 *     engine {@link World} at the time of invocation.
 * </p>
 *
 * <p>
 *     This class implements the GoF <strong>Adapter</strong> pattern.
 * </p>
 */
public final class EngineRenderWorldAdapter  implements IRenderWorldView {

    /**
     * Underlying engine world.
     *
     * <p>
     *     This reference is used exclusively for read-only access
     *     to the currently loaded chunks.
     * </p>
     */
    private final World world;

    /**
     * Creates a render-world adapter for the given engine world.
     *
     * @param world the engine world to expose to the rendering system
     *
     * @throws NullPointerException if {@code world} is {@code null}
     */
    public EngineRenderWorldAdapter(World world) {
        this.world = Objects.requireNonNull(world, "world must not be null");
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     Returns a collection of renderable chunk views corresponding
     *     to all chunks currently loaded in the engine world.
     * </p>
     *
     * <p>
     *     Each chunk is wrapped in an {@link EngineChunkRenderAdapter},
     *     ensuring that the renderer never gains access to engine-internal
     *     data structures.
     * </p>
     *
     * <p>
     *     This method performs no world mutation and MUST NOT trigger
     *     chunk loading or generation.
     * </p>
     */
    @Override
    public Collection<IRenderChunkView> getRenderableChunks() {
        return world.getChunks().values().stream()
                .map(chunk -> (IRenderChunkView)
                        new EngineChunkRenderAdapter(chunk))
                .toList();
    }


    /**
     * {@inheritDoc}
     *
     * <p>
     *     The value is derived directly from the engine world bounds.
     * </p>
     */
    @Override
    public int getMinY() {
        return World.MIN_Y;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     The value is derived directly from the engine world bounds.
     * </p>
     */
    @Override
    public int getMaxY() {
        return World.MAX_Y;
    }
}
