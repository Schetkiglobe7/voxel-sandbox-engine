package com.voxelsandbox.rendersystem.core.frame;

import java.util.Objects;


/**
 * Strongly-typed identifier for a value stored inside a {@link RenderFrame}.
 *
 * <p>
 * A {@code FrameKey} defines the semantic identity and ownership
 * of a piece of render data within a single frame.
 * </p>
 *
 * <h2>Ownership rules</h2>
 * <ul>
 *     <li> Each {@code FrameKey} has exactly one producer per frame </li>
 *     <li> The producer is the render stage declaring the key as output </li>
 *     <li> A key MUST NOT be written more than once per frame </li>
 * </ul>
 *
 * <h2>Lifetime</h2>
 * <p>
 * Values associated with a {@code FrameKey} exist only for the
 * lifetime of the {@link RenderFrame} that contains them.
 * </p>
 *
 * <p>
 * Implementations MUST NOT retain references to frame values
 * beyond the execution of a render stage.
 * </p>
 *
 * <h2>Mutability</h2>
 * <p>
 * The key itself is immutable.
 * The value associated with the key MAY be mutable, but any
 * mutability must be explicitly documented by the producing stage.
 * </p>
 *
 * <p>
 * Responsibility for safe mutation lies with the producer of the key.
 * </p>
 *
 * @param <T> the type of value associated with this key
 */
public final class FrameKey<T> {

    /**
     * Human-readable name for debugging and diagnostics.
     *
     * <p>
     *     This name has no semantic meaning and is not used
     *     for equality or lookup.
     * </p>
     */
    private final String name;

    private FrameKey(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    /**
     * Creates a new typed frame key.
     *
     * <p>
     *     The returned key instance uniquely identifies a frame value.
     * </p>
     *
     * @param name descriptive name (for debugging only)
     * @return new frame key
     */
    public static <T> FrameKey<T> of(String name) {
        return new FrameKey<T>(name);
    }

    /**
     * Returns the debug name of this key.
     *
     * @return key name
     */
    public String name() {
        return this.name;
    }


    @Override
    public String toString() {
        return "FrameKey{" +
                "name='" + name + '\'' +
                '}';
    }
}
