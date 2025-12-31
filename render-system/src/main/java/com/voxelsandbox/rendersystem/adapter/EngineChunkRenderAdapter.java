package com.voxelsandbox.rendersystem.adapter;

import com.voxelsandbox.engine.world.chunk.Chunk;
import com.voxelsandbox.engine.world.chunk.LocalVoxelPosition;
import com.voxelsandbox.engine.world.chunk.config.ChunkDimensions;
import com.voxelsandbox.rendersystem.core.chunk.IRenderChunkView;

import java.util.Objects;


/**
 * Adapter that exposes an engine {@link Chunk} as a {@link IRenderChunkView}.
 *
 * <p>
 *     This class implements the GoF <strong>Adapter</strong> pattern,
 *     providing a stable, read-only projection of engine chunk data
 *     suitable for rendering.
 * </p>
 *
 * <p>
 *     The adapter forms a strict boundary between the engine core
 *     and the rendering system:
 * </p>
 * <ul>
 *     <li> it does not expose engine-internal types </li>
 *     <li> it does not allow world mutation </li>
 *     <li> it does not trigger chunk loading or generation </li>
 * </ul>
 *
 * <p>
 *     All data returned by this adapter reflects the current state
 *     of the underlying engine chunk at the time of access.
 * </p>
 */
public class EngineChunkRenderAdapter implements IRenderChunkView {

    private final Chunk chunk;

    /**
     * Creates a render adapter for the given engine chunk.
     *
     * @param chunk the engine chunk to adapt
     *
     * @throws NullPointerException if {@code chunk} is {@code null}
     */
    public EngineChunkRenderAdapter(Chunk chunk) {
        this.chunk = Objects.requireNonNull(chunk, "chunk must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChunkX() {
        return chunk.getPosition().x();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChunkY() {
        return chunk.getPosition().y();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChunkZ() {
        return chunk.getPosition().z();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     This implementation assumes <strong>cubic chunks</strong>,
     *     returning a single uniform chunk size derived from
     *     {@link ChunkDimensions}.
     * </p>
     *
     * <p>
     *     This is a deliberate design choice for the current engine version.
     *     Future versions may replace this with per-axis dimensions
     *     without breaking the render boundary.
     * </p>
     */
    @Override
    public int getChunkSize() {
        return ChunkDimensions.CHUNK_SIZE;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     This method performs a pure read from the underlying engine chunk
     *     and MUST NOT trigger chunk loading or generation.
     * </p>
     */
    @Override
    public int getVoxel(int x, int y, int z) {
        return chunk.getVoxel(new LocalVoxelPosition(x, y, z)).ordinal();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     The returned identifier is stable for the lifetime of the chunk
     *     within a single engine session.
     * </p>
     *
     * <p>
     *     It is intended for render-side caching, mesh reuse, and dirty
     *     tracking, but MUST NOT be used as a persistent identifier
     *     across application restarts.
     * </p>
     */
    @Override
    public long getChunkId() {
        return chunk.getPosition().hashCode();
    }
}
