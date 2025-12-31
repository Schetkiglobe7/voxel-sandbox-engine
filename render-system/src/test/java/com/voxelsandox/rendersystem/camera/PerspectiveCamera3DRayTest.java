package com.voxelsandox.rendersystem.camera;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.camera.PerspectiveCamera3D;
import com.voxelsandbox.rendersystem.core.math.*;
import com.voxelsandbox.rendersystem.core.raycast.*;
import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PerspectiveCamera3DRayTest {

    private static final IVoxelWorldView INFINITE_WORLD = new IVoxelWorldView() {

        @Override
        public boolean isSolid(int x, int y, int z) {
            return false;
        }

        @Override
        public boolean isChunkLoaded(int voxelX, int voxelY, int voxelZ) {
            return true;
        }
    };

    /* ==========================================================
     * Camera → Ray
     * ========================================================== */

    @Test
    void centerScreenGeneratesForwardRay() {

        Vec3f position = new CpuVec3f(0f, 0f, 0f);
        Vec3f forward  = new CpuVec3f(0f, 0f, -1f);
        Vec3f up       = new CpuVec3f(0f, 1f, 0f);

        ICamera3D camera = new PerspectiveCamera3D(
                position,
                forward,
                up,
                (float) Math.toRadians(60),
                800f / 600f,
                0.1f,
                1000f
        );

        Ray3f ray = camera.generateRay(
                400, 300,
                800, 600
        );

        assertEquals(position, ray.origin());

        Vec3f d = ray.direction();
        assertEquals(0f, d.x(), 1e-4f);
        assertEquals(0f, d.y(), 1e-4f);
        assertEquals(-1f, d.z(), 1e-4f);
        assertEquals(1f, d.length(), 1e-4f);
    }

    /* ==========================================================
     * DDA traversal order
     * ========================================================== */

    @Test
    void traversalVisitsVoxelsInOrder() {

        Ray3f ray = new CpuRay3f(
                new CpuVec3f(0.5f, 0.5f, 0.5f),
                new CpuVec3f(1, 0, 0).normalize()
        );

        List<Integer> visitedX = new ArrayList<>();

        IVoxelRayTraversal traversal = new CpuVoxelRayTraversal();

        VoxelHitPredicate neverSolid = (x, y, z) -> false;

        VoxelVisitor visitor = (x, y, z, t, o, d) -> {
            visitedX.add(x);
            return visitedX.size() < 3;
        };

        traversal.traverse(
                ray,
                5f,
                INFINITE_WORLD,
                neverSolid,
                visitor
        );

        assertEquals(List.of(0, 1, 2), visitedX);
    }

    /* ==========================================================
     * Stop on solid voxel
     * ========================================================== */

    @Test
    void traversalStopsOnSolidVoxel() {

        Ray3f ray = new CpuRay3f(
                new CpuVec3f(0.1f, 0.5f, 0.5f),
                new CpuVec3f(1, 0, 0).normalize()
        );

        List<Integer> visited = new ArrayList<>();

        VoxelHitPredicate solidAtX2 = (x, y, z) -> x == 2;

        VoxelVisitor visitor = (x, y, z, t, o, d) -> {
            visited.add(x);
            return true;
        };

        new CpuVoxelRayTraversal().traverse(
                ray,
                10f,
                INFINITE_WORLD,
                solidAtX2,
                visitor
        );

        assertEquals(List.of(0, 1, 2), visited);
    }

    /* ==========================================================
     * traceFirstHit
     * ========================================================== */

    @Test
    void returnsHitResultOnSolidVoxel() {

        Ray3f ray = new CpuRay3f(
                new CpuVec3f(0.5f, 0.5f, 0.5f),
                new CpuVec3f(1, 0, 0).normalize()
        );

        VoxelHitPredicate solidAtX2 = (x, y, z) -> x == 2;

        IVoxelRayTraversal traversal = new CpuVoxelRayTraversal();

        Optional<VoxelHitResult> hit =
                traversal.traceFirstHit(
                        ray,
                        5f,
                        INFINITE_WORLD,
                        solidAtX2
                );

        assertTrue(hit.isPresent());

        VoxelHitResult r = hit.get();

        assertEquals(2, r.voxelX);
        assertEquals(0, r.voxelY);
        assertEquals(0, r.voxelZ);
        assertEquals(new CpuVec3f(-1, 0, 0), r.normal);
    }
}
