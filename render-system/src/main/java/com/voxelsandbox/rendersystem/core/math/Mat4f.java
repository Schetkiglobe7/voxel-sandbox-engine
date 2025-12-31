package com.voxelsandbox.rendersystem.core.math;

/**
 * Immutable 4x4 matrix used for 3D transformations.
 *
 * <p>
 *     This interface represents a mathematical 4x4 matrix suitable for
 *     expressing affine and projective transformations in 3D space,
 *     such as view, projection, and model matrices.
 * </p>
 *
 * <p>
 *     Matrices are assumed to be stored in <strong>column-major order</strong>,
 *     consistent with standard graphics pipelines (e.g. OpenGL conventions).
 * </p>
 *
 * <p>
 *     Implementations MUST be immutable.
 *     All operations must return new {@code Mat4f} instances.
 * </p>
 */
public interface Mat4f {


    /* ==========================================================
     * Access
     * ========================================================== */

    /**
     * Returns the matrix element at the given row and column.
     *
     * <p>
     *     Indices are zero-based and must be in the range {@code [0..3]}.
     * </p>
     *
     * @param row row index
     * @param col column index
     * @return matrix value at (row, column)
     *
     * @throws IndexOutOfBoundsException if indices are out of range
     */
    float get(int row, int col);

    /**
     * Returns the underlying matrix data as a flat array
     * in column-major order.
     *
     * <p>
     *     The returned array MUST be a defensive copy and must not expose
     *     internal mutable state.
     * </p>
     *
     * @return 16-element float array in column-major order
     */
    float[] toArray();

    /* ==========================================================
     * Matrix operations
     * ========================================================== */

    /**
     * Returns the matrix product {@code this * other}.
     *
     * <p>
     *     Matrix multiplication follows standard linear algebra rules
     *     and is not commutative.
     * </p>
     *
     * @param other the right-hand matrix
     * @return resulting matrix product
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    Mat4f multiply(Mat4f other);

    /**
     * Returns the inverse of this matrix.
     *
     * <p>
     *     The inverse matrix {@code M⁻¹} satisfies:
     *     {@code M * M⁻¹ = I}.
     * </p>
     *
     * @return inverted matrix
     *
     * @throws IllegalStateException if the matrix is not invertible
     */
    Mat4f inverse();

    /**
     * Transforms a 3D vector assuming a homogeneous {@code w = 1}.
     *
     * <p>
     *     This method is intended for transforming positions.
     *     Translation components of the matrix are applied.
     * </p>
     *
     * @param v input vector
     * @return transformed position vector
     *
     * @throws NullPointerException if {@code v} is {@code null}
     */
    Vec3f transformPosition(Vec3f v);

    /**
     * Transforms a 3D vector assuming a homogeneous {@code w = 0}.
     *
     * <p>
     *     This method is intended for transforming directions or normals.
     *     Translation components of the matrix are ignored.
     * </p>
     *
     * @param v input vector
     * @return transformed direction vector
     *
     * @throws NullPointerException if {@code v} is {@code null}
     */
    Vec3f transformDirection(Vec3f v);
}