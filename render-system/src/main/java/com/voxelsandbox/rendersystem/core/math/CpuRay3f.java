package com.voxelsandbox.rendersystem.core.math;

import java.util.Objects;


/**
 * CPU-side immutable implementation of a 3D ray.
 *
 * <p>
 *     This class provides a concrete {@link Ray3f} implementation
 *     backed by immutable {@link Vec3f} instances.
 * </p>
 *
 * <p>
 *     A ray is defined by:
 * </p>
 * <ul>
 *     <li> an <strong>origin</strong> point in world space </li>
 *     <li> a <strong>direction</strong> vector </li>
 * </ul>
 *
 * <p>
 *     This implementation is purely mathematical and does not depend
 *     on any rendering backend or spatial acceleration structure.
 * </p>
 *
 * <p>
 *     Typical use cases include:
 * </p>
 * <ul>
 *     <li> CPU-based ray casting </li>
 *     <li> voxel traversal (DDA) </li>
 *     <li> reference validation for GPU parity </li>
 *     <li> deterministic unit and integration testing </li>
 * </ul>
 *
 * <p>
 *     Instances of this class are:
 * </p>
 * <ul>
 *     <li> immutable </li>
 *     <li> thread-safe </li>
 *     <li> allocation-free after construction </li>
 * </ul>
 */
public class CpuRay3f implements Ray3f {

    /**
     * Ray origin in world space.
     */
    private final Vec3f origin;

    /**
     * Ray direction vector.
     *
     * <p>
     *     The direction is expected to be normalized (unit length).
     *     This class does not enforce normalization and assumes
     *     the caller respects the {@link Ray3f} contract.
     * </p>
     */
    private final Vec3f direction;

    /**
     * Creates a new immutable 3D ray.
     *
     * @param origin ray origin in world space
     * @param direction ray direction (expected to be normalized)
     *
     * @throws NullPointerException if {@code origin} or {@code direction} is {@code null}
     */
    public CpuRay3f(Vec3f origin, Vec3f direction) {
        this.origin = Objects.requireNonNull(origin, "origin must not be null");
        this.direction = Objects.requireNonNull(direction, "direction must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f origin() {
        return origin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3f direction() {
        return direction;
    }
}
