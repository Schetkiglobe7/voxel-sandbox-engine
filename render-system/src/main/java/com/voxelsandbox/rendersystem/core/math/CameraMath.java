package com.voxelsandbox.rendersystem.core.math;


/**
 * Mathematical utilities for camera-related transformations.
 *
 * <p>
 *     This class provides <strong>pure</strong> and <strong>deterministic</strong>
 *     mathematical functions used by camera implementations to compute
 *     view and projection matrices.
 * </p>
 *
 * <p>
 *     The methods defined here:
 * </p>
 * <ul>
 *     <li> perform no allocations beyond returned objects </li>
 *     <li> have no side effects </li>
 *     <li> do not depend on any rendering backend </li>
 * </ul>
 *
 * <p>
 *     All matrices are returned in <strong>column-major order</strong>
 *     and follow <strong>OpenGL conventions</strong>.
 * </p>
 */
public final class CameraMath {

    private CameraMath() {}

    /* ==========================================================
     * View matrix (LookAt)
     * ========================================================== */

    /**
     * Computes a right-handed view matrix using the classic LookAt convention.
     *
     * <p>
     *     The resulting matrix transforms world-space coordinates
     *     into camera (view) space.
     * </p>
     *
     * <p>
     *     Coordinate system assumptions:
     * </p>
     * <ul>
     *     <li> right-handed system </li>
     *     <li> camera looks toward the negative Z axis </li>
     *     <li> +Y is up </li>
     * </ul>
     *
     * @param eye    camera position in world space
     * @param center target point the camera is looking at
     * @param up     up direction vector (must be normalized)
     *
     * @return immutable view matrix in column-major order
     *
     * @throws IllegalStateException if direction vectors cannot be normalized
     */
    public static Mat4f lookAt(Vec3f eye, Vec3f center, Vec3f up) {
        Vec3f f = center.sub(eye).normalize();          // forward
        Vec3f s = f.cross(up).normalize();              // right
        Vec3f u = s.cross(f);                           // recalculated up

        float[] m = new float[16];

        // Column 0
        m[0] = s.x();
        m[1] = u.x();
        m[2] = -f.x();
        m[3] = 0f;

        // Column 1
        m[4] = s.y();
        m[5] = u.y();
        m[6] = -f.y();
        m[7] = 0f;

        // Column 2
        m[8] = s.z();
        m[9] = u.z();
        m[10] = -f.z();
        m[11] = 0f;

        // Column 3 (translation)
        m[12] = -s.dot(eye);
        m[13] = -u.dot(eye);
        m[14] = f.dot(eye);
        m[15] = 1f;

        return new CpuMat4f(m);
    }

    /* ==========================================================
     * Perspective projection
     * ========================================================== */

    /**
     * Computes a perspective projection matrix.
     *
     * <p>
     *     This matrix maps view-space coordinates into clip space
     *     using a vertical field of view.
     * </p>
     *
     * <p>
     *     Clip space Z range:
     * </p>
     * <ul>
     *     <li> {@code [-1, +1]} (OpenGL compatible) </li>
     * </ul>
     *
     * @param fovYRadians vertical field of view in radians
     * @param aspect      aspect ratio (width / height)
     * @param near        near clipping plane distance (> 0)
     * @param far         far clipping plane distance (> near)
     *
     * @return immutable projection matrix in column-major order
     *
     * @throws IllegalArgumentException if parameters are invalid
     */
    public static Mat4f perspective(
            float fovYRadians,
            float aspect,
            float near,
            float far
    ) {

        float f = (float) (1.0 / Math.tan(fovYRadians * 0.5));

        float[] m = new float[16];

        m[0]  = f / aspect;
        m[1]  = 0f;
        m[2]  = 0f;
        m[3]  = 0f;

        m[4]  = 0f;
        m[5]  = f;
        m[6]  = 0f;
        m[7]  = 0f;

        m[8]  = 0f;
        m[9]  = 0f;
        m[10] = (far + near) / (near - far);
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = (2f * far * near) / (near - far);
        m[15] = 0f;

        return new CpuMat4f(m);
    }
}
