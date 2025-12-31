package com.voxelsandbox.rendersystem.core.math;

import java.util.Objects;


/**
 * CPU-based immutable implementation of {@link Vec3f}.
 *
 * <p>
 *     This class represents a 3D vector with single-precision components
 *     and provides a pure, deterministic CPU implementation of all
 *     vector operations.
 * </p>
 *
 * <p>
 *     Instances of this class are <strong>immutable</strong>.
 *     All modifying operations return new {@code CpuVec3f} instances.
 * </p>
 *
 * <p>
 *     This implementation is intended for:
 * </p>
 * <ul>
 *     <li> CPU-based rendering </li>
 *     <li> camera and math computations </li>
 *     <li> reference implementations </li>
 *     <li> testing and validation </li>
 * </ul>
 */
public final class CpuVec3f implements Vec3f {
    private final float x;
    private final float y;
    private final float z;

    /**
     * Creates a new 3D vector with the given components.
     *
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public CpuVec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float x() {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float y() {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float z() {
        return z;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f add(Vec3f other) {
        Objects.requireNonNull(other, "other vector must not be null");
        return new CpuVec3f(
                x + other.x(),
                y + other.y(),
                z + other.z()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f sub(Vec3f other) {
        Objects.requireNonNull(other, "other vector must not be null");
        return new CpuVec3f(
                x - other.x(),
                y - other.y(),
                z - other.z()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f mul(float scalar) {
        return new CpuVec3f(
                x * scalar,
                y * scalar,
                z * scalar
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float dot(Vec3f other) {
        Objects.requireNonNull(other, "other vector must not be null");
        return
                x * other.x() +
                        y * other.y() +
                        z * other.z();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f cross(Vec3f other) {
        Objects.requireNonNull(other, "other vector must not be null");
        return new CpuVec3f(
                y * other.z() - z * other.y(),
                z * other.x() - x * other.z(),
                x * other.y() - y * other.x()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     This method does not modify this instance.
     * </p>
     *
     * @throws IllegalStateException if the vector has zero length
     */
    @Override
    public Vec3f normalize() {
        float len = length();
        if (len == 0f) {
            throw new IllegalStateException("Cannot normalize zero-length vector");
        }
        float inv = 1.0f / len;
        return new CpuVec3f(
                x * inv,
                y * inv,
                z * inv
        );
    }

    @Override
    public String toString() {
        return "CpuVec3f[" + x + ", " + y + ", " + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec3f v)) return false;
        return Float.compare(v.x(), x) == 0 &&
                Float.compare(v.y(), y) == 0 &&
                Float.compare(v.z(), z) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(x);
        result = 31 * result + Float.hashCode(y);
        result = 31 * result + Float.hashCode(z);
        return result;
    }
}
