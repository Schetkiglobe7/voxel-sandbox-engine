package com.voxelsandbox.rendersystem.core.pipeline.stage;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.pipeline.stage.camera.CameraFrameKeys;

import java.util.Set;


/**
 * Render stage responsible for exposing camera-derived
 * transformation matrices into render frame.
 *
 * <p>
 *     This stage reads the active {@code Icamera3D} and produces
 *     view and projection matrices for downstream stages.
 * </p>
 *
 * <p>
 *     This stage is purely mathematical and backend-agnostic.
 * </p>
 */
public interface CameraStage extends IRenderStage {

    @Override
    default Set<FrameKey<?>> getRequiredInputs() {
        return Set.of(CameraFrameKeys.CAMERA);
    }

    @Override
    default Set<FrameKey<?>> getProducedOutputs() {
        return Set.of(
                CameraFrameKeys.VIEW_MATRIX,
                CameraFrameKeys.PROJECTION_MATRIX
        );
    }

    @Override
    default String getId() {
        return "camera";
    }
}
