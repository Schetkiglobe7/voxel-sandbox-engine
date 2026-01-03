package com.voxelsandbox.rendersystem.core.cpu.pipeline;

import com.voxelsandbox.rendersystem.core.frame.RenderFrame;
import com.voxelsandbox.rendersystem.core.pipeline.IRenderPipeline;
import com.voxelsandbox.rendersystem.core.pipeline.stage.IRenderStage;

import java.util.List;
import java.util.Objects;


/**
 * CPU reference implementation of {@link IRenderPipeline}.
 *
 * <p>
 *     This pipeline executes render stages sequentially
 *     on a single thread using a shared {@link RenderFrame}.
 * </p>
 *
 * <p>
 *     This implementation is intended for:
 * </p>
 * <ul>
 *     <li> validating pipeline semantics </li>
 *     <li> testing render stage composition </li>
 *     <li> serving as a reference for future GPU pipelines </li>
 * </ul>
 *
 * <p>
 *     This class is <strong>not</strong> optimized for performance.
 * </p>
 */
public class CpuRenderPipeline implements IRenderPipeline {

    private final List<IRenderStage> stages;

    /**
     * Creates a new CPU render pipeline with the given ordered stages.
     *
     * @param stages ordered list of render stages
     *
     * @throws NullPointerException if {@code stages} is {@code null}
     * @throws IllegalArgumentException if {@code stages} is empty
     */
    public CpuRenderPipeline(List<IRenderStage> stages) {
        Objects.requireNonNull(stages, "stages must not be null");
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("Pipeline must contain at least one stage");
        }
        this.stages = List.copyOf(stages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(RenderFrame frame) {
        Objects.requireNonNull(frame, "frame must not be null");

        for (IRenderStage stage : stages) {
            Objects.requireNonNull(stage, "stage must not be null");

            // 1 Declare execution contract
            frame.beginStage(
                    stage.getId(),
                    stage.getRequiredInputs(),
                    stage.getProducedOutputs()
            );

            // 2 Optional pre-stage validation hook
            frame.validateBeforeStage(stage.getId());

            // 3 Execute stage
            stage.execute(frame);

            // 4 Post-stage validation
            frame.validateAfterStage(stage.getId());
        }

        // 5 End-of-frame validation
        frame.validateEndOfFrame();
    }
}
