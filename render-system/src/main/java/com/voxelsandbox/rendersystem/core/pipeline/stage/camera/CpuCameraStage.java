package com.voxelsandbox.rendersystem.core.pipeline.stage.camera;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.math.Mat4f;
import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;

import java.util.Set;


/**
 * CPU render stage responsible for deriving camera matrices.
 *
 * <p>
 *     This stage reads the active {@link ICamera3D} from the {@link RenderFrame}
 *     and produces the corrisponding view and projection matrices.
 * </p>
 *
 * <p>
 *     Responsabilities:
 * </p>
 *
 * <ul>
 *     <li> read the active camera </li>
 *     <li> compute view matrix </li>
 *     <li> compute projection matrix </li>
 *     <li> publish bot matrices and frame </li>
 * </ul>
 *
 * <p>
 *     This stage is:
 * </p>
 *
 * <ul>
 *     <li> stateless </li>
 *     <li> deterministic </li>
 *     <li> backend-agnostic </li>
 * </ul>
 */
public final class CpuCameraStage implements IRenderStage {

    private static final String ID = "camera";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Set<FrameKey<?>> getRequiredInputs() {
        return Set.of(CameraFrameKeys.CAMERA);
    }

    @Override
    public Set<FrameKey<?>> getProducedOutputs() {
        return Set.of(
                CameraFrameKeys.VIEW_MATRIX,
                CameraFrameKeys.PROJECTION_MATRIX
        );
    }

    @Override
    public void execute(RenderFrame frame) {
        ICamera3D camera = frame.get(CameraFrameKeys.CAMERA)
                .orElseThrow( () ->
                        new IllegalStateException("Camera not present in frame")
                );

        Mat4f viewMatrix = camera.getViewMatrix();
        Mat4f projectionMatrix = camera.getProjectionMatrix();

        frame.put(CameraFrameKeys.VIEW_MATRIX, viewMatrix);
        frame.put(CameraFrameKeys.PROJECTION_MATRIX, projectionMatrix);
    }


}
