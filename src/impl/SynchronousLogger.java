package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.Pipeline;
import it.menzani.logger.api.PipelineLogger;

import java.util.Optional;

public final class SynchronousLogger extends PipelineLogger {
    public SynchronousLogger() {
        super();
    }

    public SynchronousLogger(String name) {
        super(name);
    }

    @Override
    public SynchronousLogger clone() {
        return (SynchronousLogger) super.clone();
    }

    @Override
    protected SynchronousLogger newInstance() {
        return new SynchronousLogger(getName().orElse(null));
    }

    @Override
    protected void doLog(LogEntry entry) {
        for (Pipeline pipeline : getPipelines()) {
            boolean rejected = pipeline.getFilters().stream()
                    .anyMatch(filter -> filter.rejectThrowing(entry));
            if (rejected) continue;
            Optional<String> formattedEntry = pipeline.getFormatter()
                    .formatThrowing(entry, this);
            if (!formattedEntry.isPresent()) continue;
            pipeline.getConsumers()
                    .forEach(consumer -> consumer.consumeThrowing(entry, formattedEntry.get()));
        }
    }
}
