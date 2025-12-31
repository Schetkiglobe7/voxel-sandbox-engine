package com.voxelsandox.rendersystem.camera;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.camera.PerspectiveCamera3D;
import com.voxelsandbox.rendersystem.core.math.Ray3f;
import com.voxelsandbox.rendersystem.core.math.Vec3f;
import com.voxelsandbox.rendersystem.core.math.CpuVec3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Conceptual tests for screen-to-world ray generation.
 *
 * <p>
 *     These tests validate the mathematical correctness of
 *     {@link ICamera3D#generateRay(float, float, float, float)}
 *     without involving rendering or voxel logic.
 * </p>
 */
public class PerspectiveCamera3DRayTest {

    @Test
    void centerScreenGeneratesForwardRay() {

        // Arrange
        Vec3f position = new CpuVec3f(0f, 0f, 0f);
        Vec3f forward  = new CpuVec3f(0f, 0f, -1f);
        Vec3f up       = new CpuVec3f(0f, 1f, 0f);

        ICamera3D camera = new PerspectiveCamera3D(
                position,
                forward,
                up,
                (float) Math.toRadians(60.0),
                800f / 600f,
                0.1f,
                1000f
        );

        // Act
        Ray3f ray = camera.generateRay(
                400,  // center X
                300,  // center Y
                800,
                600
        );

        // Assert: origin
        assertEquals(position, ray.origin(),
                "Ray origin must match camera position");

        // Assert: direction roughly equals forward
        Vec3f dir = ray.direction();

        assertEquals(0f, dir.x(), 1e-4f);
        assertEquals(0f, dir.y(), 1e-4f);
        assertEquals(-1f, dir.z(), 1e-4f);

        // Assert: normalized
        assertEquals(1.0f, dir.length(), 1e-4f,
                "Ray direction must be normalized");
    }
}
