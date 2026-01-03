package com.voxelsandbox.rendersystem.core.pipeline.stage.ray;

import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;


/**
 * Render stage responsible for traversing ray batches
 * through the voxel world.
 *
 * <p>
 *     This stage consumes ray batches and produces a
 *     {@link RayTraversalResult} for each ray.
 * </p>
 *
 * <h2>Contract</h2>
 * <ul>
 *     <li> one result per ray </li>
 *     <li> batch order MUST NOT affect correctness </li>
 *     <li> traversal MUST be deterministic </li>
 * </ul>
 */
public interface IRayBatchTraversalStage extends IRenderStage {
    // TODO: Marker interface for now
}
