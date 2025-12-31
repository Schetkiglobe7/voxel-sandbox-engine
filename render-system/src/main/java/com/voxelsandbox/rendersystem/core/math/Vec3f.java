package com.voxelsandbox.rendersystem.core.math;

/**
 * Immutable 3D vector with single-precision components.
 *
 * <p>
 *     This interface represents a mathematical 3D vector used to model
 *     positions, directions and normals in a right-handed 3D space.
 * </p>
 *
 * <p>
 *     The interface is intentionally minimal and backend-agnostic,
 *     allowing both CPU and GPU-oriented implementations.
 * </p>
 *
 * <p>
 *     <strong>Immutability contract:</strong>
 * </p>
 * <ul>
 *     <li> implementations MUST be immutable </li>
 *     <li> all modifying operations return new {@code Vec3f} instances </li>
 *     <li> no method may alter the internal state </li>
 * </ul>
 */
public interface Vec3f {

    /**
     * Returns the X component of the vector.
     *
     * @return x component
     */
    float x();

    /**
     * Returns the Y component of the vector.
     *
     * @return y component
     */
    float y();

    /**
     * Returns the Z component of the vector.
     *
     * @return z component
     */
    float z();

    /* ==========================================================
     * Basic vector operations
     * ========================================================== */

    /* ==========================================================
     * Basic vector operations
     * ========================================================== */

    /**
     * Returns the vector sum {@code this + other}.
     *
     * <p>
     *     The operation is performed component-wise.
     * </p>
     *
     * @param other the vector to add
     * @return resulting vector
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    Vec3f add(Vec3f other);


    /**
     * Returns the vector difference {@code this - other}.
     *
     * <p>
     *     The operation is performed component-wise.
     * </p>
     *
     * @param other the vector to subtract
     * @return resulting vector
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    Vec3f sub(Vec3f other);


    /**
     * Returns this vector multiplied by a scalar.
     *
     * <p>
     *     Each component is multiplied by the given scalar.
     * </p>
     *
     * @param scalar scalar value
     * @return scaled vector
     */
    Vec3f mul(float scalar);

    /**
     * Returns the dot (scalar) product of this vector and another.
     *
     * <p>
     *     The dot product is defined as:
     *     {@code x1*x2 + y1*y2 + z1*z2}.
     * </p>
     *
     * @param other the other vector
     * @return dot product value
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    float dot(Vec3f other);

    /**
     * Returns the cross product {@code this × other}.
     *
     * <p>
     *     The resulting vector is perpendicular to both input vectors
     *     and follows the right-hand rule.
     * </p>
     *
     * @param other the other vector
     * @return cross product vector
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    Vec3f cross(Vec3f other);


    /**
     * Returns the squared length (magnitude) of the vector.
     *
     * <p>
     *     This method avoids the square root operation and is useful
     *     for performance-sensitive comparisons.
     * </p>
     *
     * @return squared vector length
     */
    float lengthSquared();

    /**
     * Returns the length (magnitude) of the vector.
     *
     * @return vector length
     */
    float length();

    /**
     * Returns a normalized (unit-length) version of this vector.
     *
     * <p>
     *     The returned vector has the same direction as this vector
     *     and a length of exactly {@code 1}.
     * </p>
     *
     * @return normalized vector
     *
     * @throws IllegalStateException if the vector has zero length
     */
    Vec3f normalize();
}
