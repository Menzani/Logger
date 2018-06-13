package it.menzani.logger.api;

import it.menzani.logger.Pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PipelineLogger extends ToggleableLogger {
    private final List<Pipeline> pipelines = new ArrayList<>();

    public List<Pipeline> getPipelines() {
        return Collections.unmodifiableList(pipelines);
    }

    public PipelineLogger setPipelines(Pipeline... pipelines) {
        this.pipelines.clear();
        Collections.addAll(this.pipelines, pipelines);
        return this;
    }

    public PipelineLogger addPipeline(Pipeline pipeline) {
        pipelines.add(pipeline);
        return this;
    }

    @Override
    public PipelineLogger clone() {
        PipelineLogger clone = newInstance();
        for (Pipeline pipeline : pipelines) {
            clone.addPipeline(pipeline.clone());
        }
        return clone;
    }

    protected abstract PipelineLogger newInstance();
}
