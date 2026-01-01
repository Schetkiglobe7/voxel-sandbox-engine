package com.voxelsandbox.rendersystem.core.frame;

import java.util.Optional;
import java.util.Set;


/**
 * Represents a single render frame and all intermediate data
 * produced during pipeline execution.
 *
 * <p>
 *     A {@code RenderFrame} acts as a shared, frame-scoped data
 *     container passed through all render stages.
 * </p>
 *
 * <p>
 *     It is responsible for:
 * </p>
 * <ul>
 *     <li> storing intermediate render results </li>
 *     <li> exposing data between pipeline stages </li>
 *     <li> enabling debugging and frame inspection </li>
 * </ul>
 *
 * <p>
 *     The frame is:
 * </p>
 * <ul>
 *     <li> mutable for the duration of a single frame </li>
 *     <li> not thread-safe by default </li>
 *     <li> reset or discarded after frame completion </li>
 * </ul>
 */
public interface RenderFrame {

    /**
     * Associates a value with the given frame key.
     *
     * <p>
     * This method MUST be invoked only by the render stage that
     * declares the key as a produced output.
     * </p>
     *
     * <p>
     * A key MAY be written exactly once per frame.
     * Any attempt to overwrite an existing key is illegal.
     * </p>
     *
     * @param key frame key
     * @param value value to associate
     * @param <T> value type
     *
     * @throws IllegalStateException if the key has already been written
     *                               during the current frame
     */
    <T> void put(FrameKey<T> key, T value);

    /**
     * Retrieves the value associated with the given frame key.
     *
     * @param key frame key
     * @return an Optional containing the value, or empty if not present
     */
    <T> Optional<T> get(FrameKey<T> key);

    /**
     * Checks wether a value is present for the given key.
     *
     * @param key typed frame key.
     * @return {@code true} if a value exists, {@code false} otherwise
     */
    boolean contains(FrameKey<?> key);

    /**
     * Validates frame state before executing a render stage.
     *
     * <p>
     * Intended to be invoked by the render pipeline.
     * Implementations may perform consistency checks
     * or fail fast on invalid state.
     * </p>
     *
     * @param stageId identifier of the stage about to execute
     */
    default void validateBeforeStage(String stageId) {
        // no-op by default
    }

    /**
     * Validates the final state of the frame after all stages
     * have been executed.
     *
     * <p>
     * This hook is intended for full-frame consistency checks.
     * </p>
     */
    default void validateEndOfFrame() {
        // no-op by default
    }

    /**
     * Validates frame state after executing a render stage.
     *
     * <p>
     * Intended to verify that all declared outputs have been produced
     * and that no illegal mutations occurred.
     * </p>
     *
     * @param stageId identifier of the stage that just executed
     */
    default void validateAfterStage(String stageId) {
        // no-op by default
    }

    /**
     * Notifies the frame that a render stage is about to execute.
     *
     * <p>
     *     This method establishes the execution contract for the stage,
     *     allowing the frame to enforce input/output constraints.
     * </p>
     *
     * @param stageId stage identifier
     * @param requiredInputs keys that must already exist in the frame
     * @param producedOutputs keys that must be written by the stage
     */
    void beginStage(
            String stageId,
            Set<FrameKey<?>> requiredInputs,
            Set<FrameKey<?>> producedOutputs
    );

    /**
     * Clears all data stored in this frame.
     *
     * <p>
     *     Intended to be called once the frame has completed execution.
     * </p>
     */
    void clear();
}
