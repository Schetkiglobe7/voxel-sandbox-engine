package com.voxelsandbox.rendersystem.core.pipeline;

import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;


/**
 * Defines a deterministic render pipeline composed of ordered render stages.
 *
 * <p>
 *     A render pipeline is responsible for orchestrating the execution
 *     of {@link IRenderStage} instances over a shared {@link RenderFrame}.
 * </p>
 *
 * <p>
 *     The pipeline:
 * </p>
 * <ul>
 *     <li> controls execution order </li>
 *     <li> owns the {@link RenderFrame} lifecycle </li>
 *     <li> invokes frame validation hooks </li>
 *     <li> is agnostic to rendering APIs and resources </li>
 * </ul>
 *
 * <p>
 *     Render stages are expected to be:
 * </p>
 * <ul>
 *     <li> stateless </li>
 *     <li> deterministic </li>
 *     <li> side effect free outside the {@link RenderFrame} </li>
 * </ul>
 */
public interface IRenderPipeline {

    /**
     * Executes the pipeline on the given render frame.
     *
     * <p>
     *     The pipeline MUST:
     * </p>
     * <ol>
     *     <li> execute stages in deterministic order </li>
     *     <li> invoke frame validation hooks before and after each stage </li>
     *     <li> invoke end-of-frame validation after the last stage </li>
     * </ol>
     *
     * <p>
     *     The pipeline MUST NOT:
     * </p>
     * <ul>
     *     <li> skip stages </li>
     *     <li> reorder stages </li>
     *     <li> mutate external state </li>
     * </ul>
     *
     * <p>
     *     The pipeline MUST validate that all required inputs declared
     *     by each {@link IRenderStage} are present in the {@link RenderFrame}
     *     before executing the stage.
     * </p>
     *
     * <p>
     *     After executing each stage, the pipeline MUST verify that all
     *     frame keys declared as produced outputs have been written
     *     to the {@link RenderFrame}.
     * </p>
     *
     *  *
     *  * @throws IllegalStateException if a stage requires a missing FrameKey
     *
     * @param frame render frame shared across stages
     *
     * @throws NullPointerException if {@code frame} is {@code null}
     */
    void execute(RenderFrame frame);
}
