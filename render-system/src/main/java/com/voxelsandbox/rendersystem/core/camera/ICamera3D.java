package com.voxelsandbox.rendersystem.core.camera;

import com.voxelsandbox.rendersystem.core.math.Mat4f;
import com.voxelsandbox.rendersystem.core.math.Ray3f;
import com.voxelsandbox.rendersystem.core.math.Vec3f;


/**
 * Defines a backend-agnostic 3D camera contract.
 *
 * <p>
 *     A camera describes how the 3D world is projected onto a 2D render target.
 *     This interface is purely mathematical and does not depend on any
 *     rendering API or windowing system.
 * </p>
 *
 * <p>
 *     Coordinate system:
 * </p>
 *
 * <ul>
 *     <li> right-handed </li>
 *     <li> X → right </li>
 *     <li> Y → up </li>
 *     <li> Z → forward (world space) </li>
 *     <li> camera looks along the negative Z axis by convention </li>
 * </ul>
 */
public interface ICamera3D {

    /* ==========================================================
     * Spatial properties
     * ========================================================== */
    /**
     * Returns the camera position in world space.
     *
     * @return camera position
     */
    Vec3f getPosition();

    /**
     * Returns the forward direction vector.
     *
     * <p>
     *     This vector points in the direction the camera is facing.
     *     It is expected to be normalized.
     * </p>
     *
     * @return forward direction
     */
    Vec3f getForward();


    /**
     * Returns the up direction vector.
     *
     * <p>
     *     This vector defines the camera's vertical orientation
     *     and is expected to be normalized.
     * </p>
     *
     * @return up direction
     */
    Vec3f getUp();

    /**
     * Returns the right direction vector.
     *
     * <p>
     *     This vector is typically derived as:
     *     {@code right = forward × up}.
     * </p>
     *
     * @return right direction
     */
    Vec3f getRight();

    /* ==========================================================
     * Projection parameters
     * ========================================================== */

    /**
     * Returns the vertical field of view in degrees.
     *
     * @return vertical FOV (degrees)
     */
    float getVerticalFov();

    /**
     * Returns the aspect ratio (width / height).
     *
     * @return aspect ratio
     */
    float getAspectRatio();

    /**
     * Returns the near clipping plane distance.
     *
     * @return near plane
     */
    float getNearPlane();

    /**
     * Returns the far clipping plane distance.
     *
     * @return far plane
     */
    float getFarPlane();

    /* ==========================================================
     * Derived matrices
     * ========================================================== */

    /**
     * Returns the view matrix.
     *
     * <p>
     *     Transforms world-space coordinates into camera (view) space.
     * </p>
     *
     * @return view matrix
     */
    Mat4f getViewMatrix();

    /**
     * Returns the projection matrix.
     *
     * <p>
     *     Transforms view-space coordinates into clip space.
     * </p>
     *
     * @return projection matrix
     */
    Mat4f getProjectionMatrix();

    /**
     * Generates a world-space ray passing through the given screen pixel.
     *
     * <p>
     *     Screen coordinates are expressed in pixel space:
     * </p>
     * <ul>
     *     <li> origin at top-left </li>
     *     <li> x increases to the right </li>
     *     <li> y increases downward </li>
     * </ul>
     *
     * @param screenX x coordinate in pixels
     * @param screenY y coordinate in pixels
     * @param viewportWidth viewport width in pixels
     * @param viewportHeight viewport height in pixels
     *
     * @return world-space ray
     */
    Ray3f generateRay(
            float screenX,
            float screenY,
            float viewportWidth,
            float viewportHeight
    );
}
