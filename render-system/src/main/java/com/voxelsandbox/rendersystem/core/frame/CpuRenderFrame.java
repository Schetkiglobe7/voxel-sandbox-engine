package com.voxelsandbox.rendersystem.core.frame;

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
 *     A {@code CpuRenderFrame} stores intermediate rendering data
 *     produced and consumed by render stages during the processing
 *     of a single frame.
 * </p>
 *
 * <p>
 *     This implementation:
 * </p>
 * <ul>
 *     <li> is frame-scoped </li>
 *     <li> is not thread-safe </li>
 *     <li> performs no implicit allocations during access </li>
 *     <li> enforces type safety via {@link FrameKey} </li>
 * </ul>
 *
 * <p>
 *     The frame acts purely as a data container and does not
 *     impose any execution or ordering semantics.
 * </p>
 */
public final class CpuRenderFrame implements RenderFrame {

    /**
     * Internal storage for frame-scoped values.
     *
     * <p>
     *     Keys are compared by identity.
     * </p>
     */
    private final Map<FrameKey<?>, Object> values = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void put(FrameKey<T> key, T value) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");

        values.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(FrameKey<T> key) {
        Objects.requireNonNull(key, "key must not be null");

        Object value = values.get(key);
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
       Objects.requireNonNull(key, "key must not be null");
       return values.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        values.clear();
    }
}
