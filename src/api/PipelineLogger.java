package it.menzani.logger.api;

import it.menzani.logger.impl.Pipeline;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class PipelineLogger extends ToggleableNamedLogger {
    private final Set<Pipeline> pipelines = new CopyOnWriteArraySet<>();

    protected PipelineLogger() {
        super();
    }

    protected PipelineLogger(String name) {
        super(name);
    }

    protected Set<Pipeline> getPipelines() {
        return Collections.unmodifiableSet(pipelines);
    }

    public Pipeline getPipeline(String name) {
        return pipelines.stream()
                .filter(pipeline -> pipeline.getName().equals(Optional.of(name))) // Implicit null check of `name`
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No pipeline found with name: " + name));
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

    public PipelineLogger profiled() {
        return profiled(profilerBuilder()
                .withLabel(getName().orElse(getClass().getSimpleName())));
    }

    public PipelineLogger profiled(ProfiledLogger.ProfilerBuilder profilerBuilder) {
        return new ProfiledLogger(profilerBuilder.withLogger(this));
    }

    @Override
    public PipelineLogger clone() {
        PipelineLogger clone = newInstance();
        pipelines.stream()
                .map(Cloneable::clone)
                .forEach(clone::addPipeline);
        return clone;
    }

    protected abstract PipelineLogger newInstance();

    public static ProfiledLogger.ProfilerBuilder profilerBuilder() {
        return new ProfiledLogger.ProfilerBuilder();
    }
}
