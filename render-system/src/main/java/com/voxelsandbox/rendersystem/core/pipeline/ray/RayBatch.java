package com.voxelsandbox.rendersystem.core.pipeline.ray;


/**
 * Represents a contiguous batch of rays inside a ray buffer.
 *
 * <p>
 *     A {@code RayBatch} does not store rays directly.
 *     Instead, it defines a logical view over a linear ray buffer
 *     using an offset and a count.
 * </p>
 *
 * <p>
 *     Batches are immutable and frame-scoped.
 * </p>
 */
public record RayBatch(int offset, int count) {

    public RayBatch {
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("count must be > 0");
        }
    }
}
