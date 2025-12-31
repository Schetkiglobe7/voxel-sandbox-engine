package com.voxelsandbox.rendersystem.core.world;

import com.voxelsandbox.rendersystem.core.chunk.IRenderChunkView;

import java.util.Collection;


/**
 * Read-only view of a voxel world for rendering purposes.
 *
 * <p>
 *     This interface represents the sole boundary between the engine
 *     world model and the rendering system.
 * </p>
 *
 * <p>
 *     Implementations MUST:
 * </p>
 * <ul>
 *     <li> be read-only </li>
 *     <li> have no side effects </li>
 *     <li> never trigger chunk loading or generation </li>
 * </ul>
 *
 * <p>
 *     This view is designed to support both CPU-only and GPU-backend
 *     rendering pipelines.
 * </p>
 */
public interface IRenderWorldView {

    /**
     * Returns all chunks currently available for rendering
     *
     * <p>
     *     The returned collection represents a snapshot of the
     *     renderable world state.
     * </p>
     *
     * <p>
     *     No guarantees are made about ordering.
     * </p>
     *
     * @return collection of renderable chunk views
     */
    Collection<IRenderChunkView> getRenderableChunks();

    /**
     * Returns the minimum world height (inclusive).
     *
     * @return minimum Y coordinate
     */
    int getMinY();

    /**
     * Returns the maximum world height (exclusive)
     *
     * @return maxium Y coordinate
     */
    int getMaxY();
}
