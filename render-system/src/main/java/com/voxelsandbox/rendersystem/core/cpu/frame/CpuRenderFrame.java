package com.voxelsandbox.rendersystem.core.cpu.frame;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;

import java.util.*;

/**
 * CPU-side reference implementation of {@link RenderFrame}.
 *
 * <p>
 * A {@code CpuRenderFrame} is a frame-scoped, mutable data container used
 * to store and exchange intermediate rendering data between render stages.
 * </p>
 *
 * <h2>Design goals</h2>
 * <ul>
 *     <li> deterministic behavior </li>
 *     <li> strict enforcement of stage input/output contracts </li>
 *     <li> clear failure modes for invalid pipeline usage </li>
 *     <li> support for debugging and frame inspection </li>
 * </ul>
 *
 * <h2>Validation modes</h2>
 * <ul>
 *     <li> {@link ValidationMode#STRICT}: fail fast on any contract violation </li>
 *     <li> {@link ValidationMode#RELAXED}: ignore violations (debug/inspection only) </li>
 * </ul>
 *
 * <p>
 * This implementation is:
 * </p>
 * <ul>
 *     <li> frame-scoped </li>
 *     <li> not thread-safe </li>
 *     <li> allocation-free during steady-state access </li>
 * </ul>
 */
public final class CpuRenderFrame implements RenderFrame {

    /**
     * Controls how strictly frame invariants are enforced.
     */
    public enum ValidationMode {
        STRICT,
        RELAXED
    }

    /* ==========================================================
     * Frame state
     * ========================================================== */

    private final ValidationMode validationMode;

    private final Map<FrameKey<?>, Object> values = new HashMap<>();
    private final List<String> executedStages = new ArrayList<>();
    private final Map<String, Set<FrameKey<?>>> stageOutputs = new LinkedHashMap<>();

    private String currentStageId;

    private Set<FrameKey<?>> expectedInputs = Set.of();
    private Set<FrameKey<?>> expectedOutputs = Set.of();
    private final Set<FrameKey<?>> writtenThisStage = new HashSet<>();

    /* ==========================================================
     * Construction
     * ========================================================== */

    /**
     * Creates a frame with {@link ValidationMode#STRICT} validation.
     */
    public CpuRenderFrame() {
        this(ValidationMode.STRICT);
    }

    /**
     * Creates a frame with the given validation mode.
     *
     * @param validationMode validation behavior
     */
    public CpuRenderFrame(ValidationMode validationMode) {
        this.validationMode = Objects.requireNonNull(
                validationMode, "validationMode must not be null"
        );
    }

    /* ==========================================================
     * RenderFrame API
     * ========================================================== */

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void put(FrameKey<T> key, T value) {
        assert currentStageId != null :
                "Frame write attempted outside of an active stage";

        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");

        if (currentStageId == null) {
            violation("Attempted to write frame key outside of an active render stage");
            return;
        }

        if (!expectedOutputs.contains(key)) {
            violation(
                    "Stage '" + currentStageId +
                            "' attempted to write undeclared output key: " + key
            );
            return;
        }

        if (!writtenThisStage.add(key)) {
            throw new IllegalStateException(
                    "Stage '" + currentStageId +
                            "' attempted to write output key twice: " + key
            );
        }

        if (values.containsKey(key)) {
            throw new IllegalStateException(
                    "Frame key already exists and cannot be overwritten: " + key
            );
        }

        values.put(key, value);
        stageOutputs.get(currentStageId).add(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(FrameKey<T> key) {
        assert currentStageId == null || expectedInputs.contains(key) :
                "Undeclared frame read in stage '" + currentStageId + "': " + key;

        Objects.requireNonNull(key, "key must not be null");

        if (currentStageId != null && !expectedInputs.contains(key)) {
            violation(
                    "Stage '" + currentStageId +
                            "' attempted to read undeclared input key: " + key
            );
            return Optional.empty();
        }

        return Optional.ofNullable((T) values.get(key));
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
    public void beginStage(
            String stageId,
            Set<FrameKey<?>> requiredInputs,
            Set<FrameKey<?>> producedOutputs
    ) {
        assert currentStageId == null :
                "beginStage called while another stage is active: " + currentStageId;

        assert writtenThisStage.isEmpty() :
                "Residual writes detected before stage start";

        assert expectedInputs.isEmpty() && expectedOutputs.isEmpty() :
                "Stage contracts not reset before beginStage";

        Objects.requireNonNull(stageId, "stageId must not be null");
        Objects.requireNonNull(requiredInputs, "requiredInputs must not be null");
        Objects.requireNonNull(producedOutputs, "producedOutputs must not be null");

        if (currentStageId != null) {
            throw new IllegalStateException(
                    "Frame already executing stage: " + currentStageId
            );
        }

        currentStageId = stageId;
        expectedInputs = Set.copyOf(requiredInputs);
        expectedOutputs = Set.copyOf(producedOutputs);
        writtenThisStage.clear();

        executedStages.add(stageId);
        stageOutputs.put(stageId, new HashSet<>());

        // Enforce presence of required inputs
        for (FrameKey<?> key : expectedInputs) {
            if (!values.containsKey(key)) {
                violation(
                        "Stage '" + stageId + "' missing required input: " + key
                );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAfterStage(String stageId) {
        Objects.requireNonNull(stageId, "stageId must not be null");

        if (!Objects.equals(stageId, currentStageId)) {
            throw new IllegalStateException(
                    "Stage mismatch: expected '" + currentStageId +
                            "', got '" + stageId + "'"
            );
        }

        if (!writtenThisStage.containsAll(expectedOutputs)) {
            violation(
                    "Stage '" + stageId +
                            "' did not write all declared outputs. " +
                            "Expected: " + expectedOutputs +
                            ", written: " + writtenThisStage
            );
        }

        assert writtenThisStage.containsAll(expectedOutputs) :
                "Stage '" + stageId + "' did not write all declared outputs";

        currentStageId = null;
        expectedInputs = Set.of();
        expectedOutputs = Set.of();
        writtenThisStage.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateEndOfFrame() {
        assert currentStageId == null :
                "Frame ended with an active stage: " + currentStageId;

        if (currentStageId != null) {
            throw new IllegalStateException(
                    "Frame ended while stage '" + currentStageId + "' was still active"
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        assert currentStageId == null :
                "Clearing frame while a stage is still active: " + currentStageId;

        if (currentStageId != null) {
            throw new IllegalStateException(
                    "Clearing frame while stage '" + currentStageId + "' is active"
            );
        }

        values.clear();
        executedStages.clear();
        stageOutputs.clear();
        writtenThisStage.clear();

        expectedInputs = Set.of();
        expectedOutputs = Set.of();
        currentStageId = null;
    }

    /* ==========================================================
     * Debug / inspection API
     * ========================================================== */

    /**
     * Returns all frame keys currently stored.
     *
     * @return immutable set of frame keys
     */
    public Set<FrameKey<?>> dumpKeys() {
        return Set.copyOf(values.keySet());
    }

    /**
     * Returns the ordered list of executed render stages.
     *
     * @return immutable list of stage identifiers
     */
    public List<String> dumpExecutedStages() {
        return List.copyOf(executedStages);
    }

    /**
     * Returns the frame keys written by each executed stage.
     *
     * @return map of stageId → written frame keys
     */
    public Map<String, Set<FrameKey<?>>> dumpStageOutputs() {
        Map<String, Set<FrameKey<?>>> snapshot = new LinkedHashMap<>();
        for (var entry : stageOutputs.entrySet()) {
            snapshot.put(entry.getKey(), Set.copyOf(entry.getValue()));
        }
        return snapshot;
    }

    /* ==========================================================
     * Internal helpers
     * ========================================================== */

    private void violation(String message) {
        if (validationMode == ValidationMode.STRICT) {
            throw new IllegalStateException(message);
        }
        // RELAXED: ignore violation
    }
}