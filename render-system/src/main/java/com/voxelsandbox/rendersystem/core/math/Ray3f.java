package com.voxelsandbox.rendersystem.core.math;


/**
 * Immutable 3D ray defined by an origin point and a direction vector.
 *
 * <p>
 *     A ray represents a half-line starting at a given origin and
 *     extending infinitely in a specified direction.
 * </p>
 *
 * <p>
 *     This interface is purely mathematical and does not depend on
 *     any rendering backend or spatial acceleration structure.
 * </p>
 *
 * <p>
 *     Contract guarantees:
 * </p>
 * <ul>
 *     <li> the ray origin is expressed in world space </li>
 *     <li> the direction vector is expected to be normalized </li>
 *     <li> implementations MUST be immutable </li>
 * </ul>
 *
 * <p>
 *     Rays are commonly used for:
 * </p>
 * <ul>
 *     <li> camera ray generation </li>
 *     <li> voxel traversal and ray casting </li>
 *     <li> intersection tests </li>
 * </ul>
 */
public interface Ray3f {

    /**
     * Returns the ray origin in world space.
     *
     * @return ray origin
     */
    Vec3f origin();

    /**
     * Returns the ray direction in world space.
     *
     * <p>
     *     The returned vector is expected to be normalized
     *     (unit length).
     * </p>
     *
     * @return normalized ray direction
     */
    Vec3f direction();
}
