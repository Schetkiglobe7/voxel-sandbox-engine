package com.voxelsandbox.rendersystem.core.pipeline.stage;

import com.voxelsandbox.rendersystem.core.frame.FrameKey;
import com.voxelsandbox.rendersystem.core.frame.RenderFrame;

import java.util.Set;


/**
 * Represents a single render stage operating on a {@link RenderFrame}.
 *
 * <p>
 * A render stage is a stateless, deterministic unit of work that transforms
 * data inside a {@link RenderFrame}.
 * </p>
 *
 * <h2>Execution model</h2>
 * <ul>
 *     <li> A stage reads a well-defined set of input keys </li>
 *     <li> A stage produces a well-defined set of output keys </li>
 *     <li> All interaction with rendering data MUST go through the frame </li>
 * </ul>
 *
 * <h2>Invariants</h2>
 * <p>
 * Implementations of this interface MUST respect the following rules:
 * </p>
 *
 * <ul>
 *     <li> The stage MUST be stateless </li>
 *     <li> The stage MUST be reentrant and thread-safe </li>
 *     <li> The stage MUST be deterministic for identical frame contents </li>
 *     <li> The stage MUST only read keys declared in {@link #getRequiredInputs()} </li>
 *     <li> The stage MUST only write keys declared in {@link #getProducedOutputs()} </li>
 *     <li> The stage MUST write all declared output keys during execution </li>
 * </ul>
 *
 * <p>
 * Violation of these rules results in undefined behavior and may be
 * detected and rejected by the render pipeline.
 * </p>
 *
 * <h2>Responsibilities not covered</h2>
 * <ul>
 *     <li> execution order </li>
 *     <li> dependency resolution </li>
 *     <li> scheduling or parallelism </li>
 *     <li> frame lifetime management </li>
 * </ul>
 *
 * <p>
 * These concerns are explicitly delegated to the render pipeline.
 * </p>
 */
public interface IRenderStage {

    /**
     * Returns the set of frame keys required by this stage.
     *
     * <p>
     * All keys returned by this method MUST be present in the
     * {@link RenderFrame} before {@link #execute(RenderFrame)} is invoked.
     * </p>
     *
     * <p>
     * Implementations MUST NOT read from any frame key that is not
     * declared here.
     * </p>
     *
     * @return immutable or effectively immutable set of required frame keys
     */
    Set<FrameKey<?>> getRequiredInputs();

    /**
     * Returns the set of frame keys produced by this stage.
     *
     * <p>
     * All keys returned by this method MUST be written to the frame
     * during execution.
     * </p>
     *
     * <p>
     * Implementations MUST NOT write to any frame key that is not
     * declared here.
     * </p>
     *
     * @return immutable or effectively immutable set of produced frame keys
     */
    Set<FrameKey<?>> getProducedOutputs();

    /**
     * Returns a stable identifier for this render stage.
     *
     * <p>
     *     The identifier is used for:
     * </p>
     * <ul>
     *     <li> debugging </li>
     *     <li> profiling </li>
     *     <li> frame inspection </li>
     *     <li> render graph validation </li>
     * </ul>
     *
     * <p>
     *     The ID must be:
     * </p>
     * <ul>
     *     <li> stable across executions </li>
     *     <li> unique within a pipeline </li>
     * </ul>
     *
     * @return stage identifier
     */
    String getId();

    /**
     * Executes this render stage.
     *
     * <p>
     * During execution, the stage may:
     * </p>
     * <ul>
     *     <li> read any key declared in {@link #getRequiredInputs()} </li>
     *     <li> write any key declared in {@link #getProducedOutputs()} </li>
     * </ul>
     *
     * <p>
     * The stage MUST NOT:
     * </p>
     * <ul>
     *     <li> read undeclared inputs </li>
     *     <li> write undeclared outputs </li>
     *     <li> rely on side effects outside the frame </li>
     * </ul>
     *
     * @param frame the render frame to operate on
     *
     * @throws IllegalStateException if required inputs are missing
     */
    void execute(RenderFrame frame);
}
