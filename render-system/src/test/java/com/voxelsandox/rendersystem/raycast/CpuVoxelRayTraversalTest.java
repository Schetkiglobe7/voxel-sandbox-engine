package com.voxelsandox.rendersystem.raycast;

import com.voxelsandbox.rendersystem.core.math.*;
import com.voxelsandbox.rendersystem.core.raycast.*;
import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CpuVoxelRayTraversalTest {
    private final CpuVoxelRayTraversal traversal = new CpuVoxelRayTraversal();
    private IVoxelWorldView world(int solidX, int lastX) {
        return new IVoxelWorldView() {
            public boolean isSolid(int x, int y, int z) { return x == solidX; }
            public boolean isChunkLoaded(int x, int y, int z) { return x <= lastX; }
        };
    }
    private CpuRay3f ray(float x, float dx) { return new CpuRay3f(new CpuVec3f(x,.5f,.5f), new CpuVec3f(dx,0,0)); }
    @Test void startingSolidHasZeroDistanceAndNormal() {
        var world = world(0, 10);
        var hit = traversal.traceFirstHit(ray(.5f,1), 10, world, world::isSolid).orElseThrow();
        assertEquals(0, hit.t);
        assertEquals(new CpuVec3f(0,0,0), hit.normal);
    }
    @Test void respectsDistanceAndUnloadedBoundary() {
        var world = world(2, 10);
        assertTrue(traversal.traceFirstHit(ray(.5f,1), 1.49f, world, world::isSolid).isEmpty());
        var hit = traversal.traceFirstHit(ray(.5f,1), 1.5f, world, world::isSolid).orElseThrow();
        assertEquals(1.5f, hit.t);
        assertEquals(new CpuVec3f(-1,0,0), hit.normal);
        var unloaded = world(2,1);
        assertTrue(traversal.traceFirstHit(ray(.5f,1),10,unloaded,unloaded::isSolid).isEmpty());
    }
    @Test void visitorReceivesEntryDistancesWithoutExceedingLimit() {
        var world = world(99,10);
        var times = new ArrayList<Float>();
        traversal.traverse(ray(.1f,1),1f,world,world::isSolid,(x,y,z,t,o,d)-> { times.add(t); return true; });
        assertEquals(List.of(0f,.9f),times);
    }
    @Test void negativeDirectionAndBoundaryAreDeterministic() {
        var world = world(-1,10);
        var hit = traversal.traceFirstHit(ray(0, -1),10,world,world::isSolid).orElseThrow();
        assertEquals(-1,hit.voxelX);
        assertEquals(0f,hit.t,0.000001f);
        assertEquals(new CpuVec3f(1,0,0),hit.normal);
    }
    @Test void invalidRaysAndDistancesFailFast() {
        var world = world(99,10);
        for (float distance : new float[]{0,-1,Float.NaN,Float.POSITIVE_INFINITY})
            assertThrows(IllegalArgumentException.class,()->traversal.traceFirstHit(ray(.5f,1),distance,world,world::isSolid));
        assertThrows(IllegalArgumentException.class,()->traversal.traceFirstHit(ray(.5f,0),10,world,world::isSolid));
    }
}
