package com.voxelsandbox.rendersystem.core.pipeline.stage.geometry;

import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;


/**
 * Render stage responsible for generating world-space rays
 * from camera and screen-space information.
 *
 * <p>
 *     A {@code IRayGenerationStage} produces one or more rays
 *     per frame, typically corresponding to pixels, tiles,
 *     or sampling patterns.
 * </p>
 *
 * <h2>Responsibilities</h2>
 *
 * <ul>
 *     <li> convert camera + projection data into world-space rays </li>
 *     <li> generate deterministic ray sets for a frame </li>
 *     <li> write ray origins and directions into the {@code RenderFrame} </li>
 * </ul>
 *
 * <h2>Non-responsibilities</h2>
 *
 * <ul>
 *     <li> ray traversal </li>
 *     <li> hit detection </li>
 *     <li> shading </li>
 *     <li> CPU resource management </li>
 * </ul>
 *
 * <p>
 *     Implementations MUST:
 * </p>
 *
 * <ul>
 *     <li> declare all required camera-related inputs </li>
 *     <li> produce {@link CameraRayFrameKeys#RAY_ORIGINS} </li>
 *     <li> produce {@link CameraRayFrameKeys#RAY_DIRECTIONS} </li>
 * </ul>
 */
public interface IRayGenerationStage  extends IRenderStage {
    //TODO: Marker interface for ray generation stages
}
