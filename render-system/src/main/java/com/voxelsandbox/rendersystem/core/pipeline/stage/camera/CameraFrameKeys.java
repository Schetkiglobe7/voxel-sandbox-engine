package com.voxelsandbox.rendersystem.core.pipeline.stage.camera;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.math.Mat4f;


/**
 * Frame keys used by camera-related render stages.
 *
 * <p>
 *     These keys define the contract between the camera stage
 *     and downstream render stages.
 * </p>
 */
public final class CameraFrameKeys {

    private CameraFrameKeys() {}

    /**
     * Active camera for the current frame.
     */
    public static final FrameKey<ICamera3D> CAMERA =
            FrameKey.of("viewMatrix");

    /**
     * View matrix derived from the camera.
     */
    public static final FrameKey<Mat4f> VIEW_MATRIX=
            FrameKey.of("viewMatrix");

    /**
     * Projection matrix derived from the camera.
     */
    public static final FrameKey<Mat4f> PROJECTION_MATRIX =
            FrameKey.of("projectionMatrix");
}
