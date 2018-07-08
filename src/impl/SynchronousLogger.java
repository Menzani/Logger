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
                    .anyMatch(filter -> doFilter(filter, entry));
            if (rejected) continue;
            Optional<String> formattedEntry = doFormat(pipeline.getFormatter(), entry);
            if (!formattedEntry.isPresent()) continue;
            pipeline.getConsumers()
                    .forEach(consumer -> doConsume(consumer, entry, formattedEntry.get()));
        }
    }
}
