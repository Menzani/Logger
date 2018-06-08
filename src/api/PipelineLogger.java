package it.menzani.logger.api;

import it.menzani.logger.Pipeline;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class PipelineLogger extends ToggleableLogger {
    private final Set<Pipeline> pipelines = new HashSet<>();

    {
        addPipeline(Pipeline.newConsoleLocalPipeline());
    }

    protected Set<Pipeline> getPipelines() {
        return pipelines;
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
}