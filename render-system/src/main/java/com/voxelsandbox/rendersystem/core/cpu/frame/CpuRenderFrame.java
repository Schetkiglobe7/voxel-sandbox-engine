package com.voxelsandbox.rendersystem.core.cpu.frame;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * CPU-side implementation of {@link RenderFrame}.
 *
 * <p>
 *     This class represents a mutable, frame-scoped data container
 *     used during CPU render pipeline execution.
 * </p>
 *
 * <p>
 *     It enforces:
 * </p>
 * <ul>
 *     <li> write-once semantics per {@link FrameKey} </li>
 *     <li> type safety via generic keys </li>
 *     <li> explicit frame lifecycle via {@link #clear()} </li>
 * </ul>
 *
 * <p>
 *     This implementation is:
 * </p>
 * <ul>
 *     <li> not thread-safe </li>
 *     <li> allocation-based (HashMap) </li>
 *     <li> intended for CPU pipelines and testing </li>
 * </ul>
 */
public final class CpuRenderFrame implements RenderFrame {

    /**
     * Internal storage for frame-scoped data.
     *
     * <p>
     *     Keys are unique per frame and values are written once.
     * </p>
     */
    private final Map<FrameKey<?>, Object> data = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void put(FrameKey<T> key, T value) {
        Objects.requireNonNull(key, "FrameKey must not be null");
        Objects.requireNonNull(value, "Frame value must not be null");

        if (data.containsKey(key)) {
            throw new IllegalStateException(
                    "FrameKey already written in this frame: " + key.hashCode()
            );
        }

        data.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(FrameKey<T> key) {
        Objects.requireNonNull(key, "FrameKey must not be null");

        Object value = data.get(key);
        if (value == null) {
            return Optional.empty();
        }

        return Optional.of((T) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(FrameKey<?> key) {
        Objects.requireNonNull(key, "FrameKey must not be null");
        return data.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        data.clear();
    }

}
