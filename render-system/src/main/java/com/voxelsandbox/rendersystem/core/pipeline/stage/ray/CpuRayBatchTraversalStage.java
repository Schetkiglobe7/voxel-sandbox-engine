package com.voxelsandbox.rendersystem.core.pipeline.stage.ray;

import com.voxelsandbox.rendersystem.core.frame.*;
import com.voxelsandbox.rendersystem.core.math.*;
import com.voxelsandbox.rendersystem.core.pipeline.stage.geometry.CameraRayFrameKeys;
import com.voxelsandbox.rendersystem.core.raycast.CpuVoxelRayTraversal;
import java.util.*;

/** Stateless traversal; results retain original ray-buffer order regardless of batch order. */
public final class CpuRayBatchTraversalStage implements IRayBatchTraversalStage {
    @Override public String getId() { return "cpu-ray-traversal"; }
    @Override public Set<FrameKey<?>> getRequiredInputs() {
        return Set.of(CameraRayFrameKeys.RAY_ORIGINS, CameraRayFrameKeys.RAY_DIRECTIONS,
                CameraRayFrameKeys.RAY_BATCHES, RayWorldFrameKeys.WORLD, RayWorldFrameKeys.MAX_DISTANCE);
    }
    @Override public Set<FrameKey<?>> getProducedOutputs() { return Set.of(CameraRayFrameKeys.RAY_RESULTS); }
    @Override public void execute(RenderFrame frame) {
        var origins = frame.get(CameraRayFrameKeys.RAY_ORIGINS).orElseThrow();
        var directions = frame.get(CameraRayFrameKeys.RAY_DIRECTIONS).orElseThrow();
        var batches = frame.get(CameraRayFrameKeys.RAY_BATCHES).orElseThrow();
        var world = frame.get(RayWorldFrameKeys.WORLD).orElseThrow();
        float distance = frame.get(RayWorldFrameKeys.MAX_DISTANCE).orElseThrow();
        if (!Float.isFinite(distance) || distance <= 0) throw new IllegalArgumentException("Distance must be finite and positive");
        if (origins.size() != directions.size()) throw new IllegalArgumentException("Ray buffers differ in length");
        boolean[] covered = new boolean[origins.size()];
        for (var batch : batches) {
            if ((long) batch.offset() + batch.count() > origins.size()) throw new IllegalArgumentException("Batch outside ray buffer");
            for (int i = batch.offset(); i < batch.offset() + batch.count(); i++) {
                if (covered[i]) throw new IllegalArgumentException("Overlapping ray batches");
                covered[i] = true;
            }
        }
        for (boolean present : covered) if (!present) throw new IllegalArgumentException("Batches must cover every ray");
        var results = new RayTraversalResult[origins.size()];
        var traversal = new CpuVoxelRayTraversal();
        for (var batch : batches) {
            for (int i = batch.offset(); i < batch.offset() + batch.count(); i++) {
                var ray = new CpuRay3f(origins.get(i), directions.get(i));
                var hit = traversal.traceFirstHit(ray, distance, world, world::isSolid);
                results[i] = hit.map(h -> RayTraversalResult.hit(
                        ray.origin().add(ray.direction().mul(h.t)), h.normal, h.t))
                        .orElseGet(RayTraversalResult::miss);
            }
        }
        frame.put(CameraRayFrameKeys.RAY_RESULTS, List.of(results));
    }
}
