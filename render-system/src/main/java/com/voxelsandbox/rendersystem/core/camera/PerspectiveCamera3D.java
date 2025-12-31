package com.voxelsandbox.rendersystem.core.camera;

import com.voxelsandbox.rendersystem.core.math.*;

import java.util.Objects;


/**
 * CPU-side immutable perspective camera implementation.
 *
 * <p>
 *     This class provides a concrete, CPU-based implementation of a
 *     perspective 3D camera suitable for voxel-based rendering pipelines.
 * </p>
 *
 * <p>
 *     The camera is purely mathematical and backend-agnostic:
 * </p>
 * <ul>
 *     <li> no rendering API assumptions </li>
 *     <li> no windowing or input dependencies </li>
 *     <li> no mutable internal state </li>
 * </ul>
 *
 * <p>
 *     Coordinate system conventions:
 * </p>
 * <ul>
 *     <li> right-handed coordinate system </li>
 *     <li> +X → right </li>
 *     <li> +Y → up </li>
 *     <li> +Z → forward </li>
 * </ul>
 *
 * <p>
 *     All direction vectors provided to this camera are expected
 *     to be normalized.
 * </p>
 */
public final class PerspectiveCamera3D  implements ICamera3D {

    /**
     * Camera position in world space.
     */
    private final Vec3f position;

    /**
     * Forward direction vector.
     *
     * <p>
     *     Points in the direction the camera is facing and
     *     is expected to be normalized.
     * </p>
     */
    private final Vec3f forward;

    /**
     * Up direction vector.
     *
     * <p>
     *     Defines the vertical orientation of the camera and
     *     is expected to be normalized.
     * </p>
     */
    private final Vec3f up;

    /**
     * Vertical field of view in radians.
     *
     * <p>
     *     Stored internally in radians for mathematical computations.
     *     Converted to degrees when exposed through the public API.
     * </p>
     */
    private final float fovYRadians;

    /**
     * Viewport aspect ratio (width / height).
     */
    private final float aspectRatio;

    /**
     * Near clipping plane distance.
     */
    private final float nearPlane;

    /**
     * Far clipping plane distance.
     */
    private final float farPlane;

    /**
     * Creates a new immutable perspective camera.
     *
     * @param position camera position in world space
     * @param forward forward direction (must be normalized)
     * @param up up direction (must be normalized)
     * @param fovYRadians vertical field of view in radians
     * @param aspectRatio viewport aspect ratio (width / height)
     * @param nearPlane near clipping plane distance (> 0)
     * @param farPlane far clipping plane distance (> nearPlane)
     *
     * @throws NullPointerException if any vector parameter is {@code null}
     * @throws IllegalArgumentException if projection parameters are invalid
     */
    public PerspectiveCamera3D(
            Vec3f position,
            Vec3f forward,
            Vec3f up,
            float fovYRadians,
            float aspectRatio,
            float nearPlane,
            float farPlane
    ) {
        this.position = Objects.requireNonNull(position, "position must not be null");
        this.forward = Objects.requireNonNull(forward, "forward must not be null");
        this.up = Objects.requireNonNull(up, "up must not be null");

        if (fovYRadians <= 0f || fovYRadians >= Math.PI) {
            throw new IllegalArgumentException("Invalid vertical field of view");
        }
        if (aspectRatio <= 0f) {
            throw new IllegalArgumentException("Invalid aspect ratio");
        }
        if (nearPlane <= 0f || farPlane <= nearPlane) {
            throw new IllegalArgumentException("Invalid clip planes");
        }

        this.fovYRadians = fovYRadians;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    /* ==========================================================
     * Spatial properties
     * ========================================================== */

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f getForward() {
        return forward;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f getUp() {
        return up;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     The right vector is derived as {@code forward × up}
     *     and normalized to ensure orthonormal camera basis vectors.
     * </p>
     */
    @Override
    public Vec3f getRight() {
        return forward.cross(up).normalize();
    }

    /* ==========================================================
     * Projection parameters
     * ========================================================== */

    /**
     * {@inheritDoc}
     *
     * <p>
     *     Returned value is expressed in degrees,
     *     as required by the {@link ICamera3D} contract.
     * </p>
     */
    @Override
    public float getVerticalFov() {
        return (float) Math.toDegrees(fovYRadians);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getAspectRatio() {
        return aspectRatio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getNearPlane() {
        return nearPlane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFarPlane() {
        return farPlane;
    }

    /* ==========================================================
     * Derived matrices
     * ========================================================== */

    /**
     * {@inheritDoc}
     */
    @Override
    public Mat4f getViewMatrix() {
        return CameraMath.lookAt(
                position,
                position.add(forward),
                up
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mat4f getProjectionMatrix() {
        return CameraMath.perspective(
                fovYRadians,
                aspectRatio,
                nearPlane,
                farPlane
        );
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     The generated ray originates from the camera position and
     *     passes through the specified screen-space pixel.
     * </p>
     *
     * <p>
     *     The transformation pipeline is:
     * </p>
     * <ol>
     *     <li> screen space → normalized device coordinates (NDC) </li>
     *     <li> NDC → view space (inverse projection) </li>
     *     <li> view space → world space (inverse view) </li>
     * </ol>
     *
     * <p>
     *     The resulting direction vector is normalized before
     *     constructing the final {@link Ray3f}.
     * </p>
     */
    @Override
    public Ray3f generateRay(
            float screenX,
            float screenY,
            float viewportWidth,
            float viewportHeight
    ) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(forward);
        Objects.requireNonNull(up);

        // 1. Screen → NDC [-1, +1]
        float ndcX = (2f * screenX) / viewportWidth - 1f;
        float ndcY = 1f - (2f * screenY) / viewportHeight;

        // 2. Clip space ray (Z = -1, W = 1)
        Vec3f rayClip = new CpuVec3f(ndcX, ndcY, -1f);

        // 3. View space (inverse projection)
        Mat4f invProj = getProjectionMatrix().inverse();
        Vec3f rayView = invProj.transformDirection(rayClip);

        // ⚠️ forza direzione forward
        Vec3f rayDirView = new CpuVec3f(
                rayView.x(),
                rayView.y(),
                -1f
        );

        // 4. World space
        Mat4f invView = getViewMatrix().inverse();
        Vec3f rayWorld = invView.transformDirection(rayDirView);

        // 5. Normalize FINALE (qui non può essere zero)
        Vec3f direction = rayWorld.normalize();

        return new CpuRay3f(position, direction);
    }
}
