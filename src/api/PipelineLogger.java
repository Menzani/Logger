package it.menzani.logger.api;

import it.menzani.logger.CloneException;
import it.menzani.logger.Pipeline;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class PipelineLogger extends ToggleableLogger {
    private final List<Pipeline> pipelines = new CopyOnWriteArrayList<>();

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
        PipelineLogger clone = newInstance().orElseThrow(() -> new CloneException(this));
        pipelines.stream()
                .map(Cloneable::clone)
                .forEach(clone::addPipeline);
        return clone;
    }

    protected Optional<? extends PipelineLogger> newInstance() {
        try {
            PipelineLogger instance = getClass().getConstructor().newInstance();
            return Optional.of(instance);
        } catch (NoSuchMethodException e) {
            printInstantiationError("find public no-arg constructor in");
            e.printStackTrace();
        } catch (InstantiationException e) {
            printInstantiationError("instantiate");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new AssertionError("Class#getConstructor() returns public members only.", e);
        } catch (InvocationTargetException e) {
            printInstantiationError("properly construct");
            e.getCause().printStackTrace();
        }
        return Optional.empty();
    }

    private void printInstantiationError(String action) {
        printLoggerError("Could not " + action + ' ' + PipelineLogger.class.getSimpleName() + ": " + getClass().getName());
    }
}
