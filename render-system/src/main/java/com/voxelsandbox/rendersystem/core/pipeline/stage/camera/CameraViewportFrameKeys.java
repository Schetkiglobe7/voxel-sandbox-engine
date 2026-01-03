package com.voxelsandbox.rendersystem.core.pipeline.stage.camera;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;


/**
 * Frame keys defining the active viewport for ray generation.
 *
 * <p>
 * Viewport data is provided by the pipeline or a bootstrap stage
 * and consumed by ray generation stages.
 * </p>
 */
public final class CameraViewportFrameKeys {

    private CameraViewportFrameKeys() {}

    /**
     * Viewport width in pixels.
     */
    public static final FrameKey<Integer> VIEWPORT_WIDTH =
            FrameKey.of("viewport.width");

    /**
     * Viewport height in pixels.
     */
    public static final FrameKey<Integer> VIEWPORT_HEIGHT =
            FrameKey.of("viewport.height");
}
