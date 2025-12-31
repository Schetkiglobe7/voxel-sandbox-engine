package com.voxelsandbox.rendersystem.core.math;

import java.util.Objects;


/**
 * CPU-side immutable 4x4 matrix implementation.
 *
 * <p>
 *     This class provides a concrete, deterministic implementation of
 *     {@link Mat4f} intended as the reference matrix math backend
 *     for CPU-based computations.
 * </p>
 *
 * <p>
 *     Matrices are stored in <strong>column-major order</strong>
 *     and follow <strong>OpenGL conventions</strong>.
 * </p>
 *
 * <p>
 *     This implementation is:
 * </p>
 * <ul>
 *     <li> immutable </li>
 *     <li> thread-safe </li>
 *     <li> allocation-bounded </li>
 *     <li> suitable for testing and reference validation </li>
 * </ul>
 */
public class CpuMat4f implements Mat4f {

    /**
     * Internal matrix storage in column-major order.
     *
     * <p>
     *     Layout:
     *     {@code m[col * 4 + row]}
     * </p>
     */
    private final float[] m; // column-major, length = 16

    /**
     * Creates a new matrix from the given column-major array.
     *
     * <p>
     *     The input array is defensively copied to preserve immutability.
     * </p>
     *
     * @param values 16-element array in column-major order
     *
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values.length != 16}
     */
    public CpuMat4f(float[] values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.length != 16) {
            throw new IllegalArgumentException("Matrix must have 16 elements");
        }
        this.m = values.clone();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public float get(int row, int col) {
        return m[col * 4 + row];
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     Returns a defensive copy of the internal matrix data.
     * </p>
     */
    @Override
    public float[] toArray() {
        return m.clone();
    }


    /**
     * {@inheritDoc}
     *
     * <p>
     *     Matrix multiplication follows standard linear algebra rules:
     *     {@code result = this × other}.
     * </p>
     *
     * <p>
     *     The operation is fully deterministic and allocates exactly
     *     one new matrix.
     * </p>
     */
    @Override
    public Mat4f multiply(Mat4f other) {
        Objects.requireNonNull(other, "other must not be null");

        float[] r = new float[16];

        for (int c = 0; c < 4; c++) {
            for (int rRow = 0; rRow < 4; rRow++) {
                float sum = 0f;
                for (int k = 0; k < 4; k++) {
                    sum += this.get(rRow, k) * other.get(k, c);
                }
                r[c * 4 + rRow] = sum;
            }
        }
        return new CpuMat4f(r);
    }

    /**
     * Computes the inverse of this matrix.
     *
     * <p>
     *     This method supports general 4x4 matrix inversion and
     *     is suitable for view and projection matrices.
     * </p>
     *
     * <p>
     *     The implementation is adapted for column-major storage
     *     and performs no intermediate allocations.
     * </p>
     *
     * @return inverse matrix
     *
     * @throws IllegalStateException if the matrix is not invertible
     */
    @Override
    public Mat4f inverse() {
        float[] inv = new float[16];
        float[] a = m;

        inv[0] = a[5]  * a[10] * a[15] -
                a[5]  * a[11] * a[14] -
                a[9]  * a[6]  * a[15] +
                a[9]  * a[7]  * a[14] +
                a[13] * a[6]  * a[11] -
                a[13] * a[7]  * a[10];

        inv[4] = -a[4]  * a[10] * a[15] +
                a[4]  * a[11] * a[14] +
                a[8]  * a[6]  * a[15] -
                a[8]  * a[7]  * a[14] -
                a[12] * a[6]  * a[11] +
                a[12] * a[7]  * a[10];

        inv[8] = a[4]  * a[9] * a[15] -
                a[4]  * a[11] * a[13] -
                a[8]  * a[5] * a[15] +
                a[8]  * a[7] * a[13] +
                a[12] * a[5] * a[11] -
                a[12] * a[7] * a[9];

        inv[12] = -a[4]  * a[9] * a[14] +
                a[4]  * a[10] * a[13] +
                a[8]  * a[5] * a[14] -
                a[8]  * a[6] * a[13] -
                a[12] * a[5] * a[10] +
                a[12] * a[6] * a[9];

        inv[1] = -a[1]  * a[10] * a[15] +
                a[1]  * a[11] * a[14] +
                a[9]  * a[2] * a[15] -
                a[9]  * a[3] * a[14] -
                a[13] * a[2] * a[11] +
                a[13] * a[3] * a[10];

        inv[5] = a[0]  * a[10] * a[15] -
                a[0]  * a[11] * a[14] -
                a[8]  * a[2] * a[15] +
                a[8]  * a[3] * a[14] +
                a[12] * a[2] * a[11] -
                a[12] * a[3] * a[10];

        inv[9] = -a[0]  * a[9] * a[15] +
                a[0]  * a[11] * a[13] +
                a[8]  * a[1] * a[15] -
                a[8]  * a[3] * a[13] -
                a[12] * a[1] * a[11] +
                a[12] * a[3] * a[9];

        inv[13] = a[0]  * a[9] * a[14] -
                a[0]  * a[10] * a[13] -
                a[8]  * a[1] * a[14] +
                a[8]  * a[2] * a[13] +
                a[12] * a[1] * a[10] -
                a[12] * a[2] * a[9];

        inv[2] = a[1]  * a[6] * a[15] -
                a[1]  * a[7] * a[14] -
                a[5]  * a[2] * a[15] +
                a[5]  * a[3] * a[14] +
                a[13] * a[2] * a[7] -
                a[13] * a[3] * a[6];

        inv[6] = -a[0]  * a[6] * a[15] +
                a[0]  * a[7] * a[14] +
                a[4]  * a[2] * a[15] -
                a[4]  * a[3] * a[14] -
                a[12] * a[2] * a[7] +
                a[12] * a[3] * a[6];

        inv[10] = a[0]  * a[5] * a[15] -
                a[0]  * a[7] * a[13] -
                a[4]  * a[1] * a[15] +
                a[4]  * a[3] * a[13] +
                a[12] * a[1] * a[7] -
                a[12] * a[3] * a[5];

        inv[14] = -a[0]  * a[5] * a[14] +
                a[0]  * a[6] * a[13] +
                a[4]  * a[1] * a[14] -
                a[4]  * a[2] * a[13] -
                a[12] * a[1] * a[6] +
                a[12] * a[2] * a[5];

        inv[3] = -a[1] * a[6] * a[11] +
                a[1] * a[7] * a[10] +
                a[5] * a[2] * a[11] -
                a[5] * a[3] * a[10] -
                a[9] * a[2] * a[7] +
                a[9] * a[3] * a[6];

        inv[7] = a[0] * a[6] * a[11] -
                a[0] * a[7] * a[10] -
                a[4] * a[2] * a[11] +
                a[4] * a[3] * a[10] +
                a[8] * a[2] * a[7] -
                a[8] * a[3] * a[6];

        inv[11] = -a[0] * a[5] * a[11] +
                a[0] * a[7] * a[9] +
                a[4] * a[1] * a[11] -
                a[4] * a[3] * a[9] -
                a[8] * a[1] * a[7] +
                a[8] * a[3] * a[5];

        inv[15] = a[0] * a[5] * a[10] -
                a[0] * a[6] * a[9] -
                a[4] * a[1] * a[10] +
                a[4] * a[2] * a[9] +
                a[8] * a[1] * a[6] -
                a[8] * a[2] * a[5];

        float det =
                a[0] * inv[0] +
                        a[1] * inv[4] +
                        a[2] * inv[8] +
                        a[3] * inv[12];

        if (det == 0f) {
            throw new IllegalStateException("Matrix is not invertible");
        }

        float invDet = 1f / det;
        for (int i = 0; i < 16; i++) {
            inv[i] *= invDet;
        }

        return new CpuMat4f(inv);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     The input vector is treated as a position with
     *     homogeneous coordinate {@code w = 1}.
     * </p>
     *
     * <p>
     *     If the resulting {@code w} is not {@code 0} or {@code 1},
     *     perspective division is applied.
     * </p>
     */
    @Override
    public Vec3f transformPosition(Vec3f v) {
        float x = v.x();
        float y = v.y();
        float z = v.z();

        float tx = get(0,0)*x + get(0,1)*y + get(0,2)*z + get(0,3);
        float ty = get(1,0)*x + get(1,1)*y + get(1,2)*z + get(1,3);
        float tz = get(2,0)*x + get(2,1)*y + get(2,2)*z + get(2,3);
        float tw = get(3,0)*x + get(3,1)*y + get(3,2)*z + get(3,3);

        if (tw != 0f && tw != 1f) {
            tx /= tw;
            ty /= tw;
            tz /= tw;
        }

        return new CpuVec3f(tx, ty, tz);
    }


    /**
     * {@inheritDoc}
     *
     * <p>
     *     The input vector is treated as a direction with
     *     homogeneous coordinate {@code w = 0}.
     * </p>
     *
     * <p>
     *     Translation components of the matrix are ignored.
     * </p>
     */
    @Override
    public Vec3f transformDirection(Vec3f v) {
        float x = v.x();
        float y = v.y();
        float z = v.z();

        float tx = get(0,0)*x + get(0,1)*y + get(0,2)*z;
        float ty = get(1,0)*x + get(1,1)*y + get(1,2)*z;
        float tz = get(2,0)*x + get(2,1)*y + get(2,2)*z;

        return new CpuVec3f(tx, ty, tz);
    }
}
