package com.voxelsandox.rendersystem.ray;

import com.voxelsandbox.rendersystem.core.cpu.frame.CpuRenderFrame;
import com.voxelsandbox.rendersystem.core.cpu.pipeline.CpuRenderPipeline;
import com.voxelsandbox.rendersystem.core.math.*;
import com.voxelsandbox.rendersystem.core.pipeline.ray.RayBatch;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.CameraRayFrameKeys;
import com.voxelsandbox.rendersystem.core.pipeline.stage.ray.*;
import com.voxelsandbox.rendersystem.core.world.IVoxelWorldView;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CpuRayBatchTraversalStageTest {
    private CpuRenderFrame execute(List<RayBatch> batches) {
        var frame = new CpuRenderFrame();
        var keys = Set.of(CameraRayFrameKeys.RAY_ORIGINS,CameraRayFrameKeys.RAY_DIRECTIONS,
                CameraRayFrameKeys.RAY_BATCHES,RayWorldFrameKeys.WORLD,RayWorldFrameKeys.MAX_DISTANCE);
        frame.beginStage("inputs",Set.of(),keys);
        frame.put(CameraRayFrameKeys.RAY_ORIGINS,List.of(new CpuVec3f(.5f,.5f,.5f),new CpuVec3f(.5f,.5f,.5f)));
        frame.put(CameraRayFrameKeys.RAY_DIRECTIONS,List.of(new CpuVec3f(1,0,0),new CpuVec3f(-1,0,0)));
        frame.put(CameraRayFrameKeys.RAY_BATCHES,batches);
        frame.put(RayWorldFrameKeys.WORLD,new IVoxelWorldView() {
            public boolean isSolid(int x,int y,int z) { return x == 2; }
            public boolean isChunkLoaded(int x,int y,int z) { return x >= -4 && x <= 4; }
        });
        frame.put(RayWorldFrameKeys.MAX_DISTANCE,10f);
        frame.validateAfterStage("inputs");
        new CpuRenderPipeline(List.of(new CpuRayBatchTraversalStage())).execute(frame);
        return frame;
    }
    @Test void batchReorderingPreservesRayOrderAndIntersectionPosition() {
        var forward = execute(List.of(new RayBatch(0,1),new RayBatch(1,1)));
        var reversed = execute(List.of(new RayBatch(1,1),new RayBatch(0,1)));
        for (var frame : List.of(forward,reversed)) {
            var results = frame.get(CameraRayFrameKeys.RAY_RESULTS).orElseThrow();
            assertEquals(2,results.size());
            assertTrue(results.get(0).isHit());
            assertEquals(new CpuVec3f(2,.5f,.5f),results.get(0).hitPosition());
            assertEquals(1.5f,results.get(0).distance());
            assertFalse(results.get(1).isHit());
            assertThrows(UnsupportedOperationException.class,()->results.clear());
        }
    }
    @Test void invalidBatchCoverageIsRejected() {
        for (var batches : List.of(List.of(new RayBatch(0,1)),List.of(new RayBatch(0,2),new RayBatch(1,1)),List.of(new RayBatch(0,3))))
            assertThrows(IllegalArgumentException.class,()->execute(batches));
    }
}
