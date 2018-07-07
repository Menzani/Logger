package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.Pipeline;
import it.menzani.logger.api.PipelineLogger;

import java.util.Optional;

public final class SynchronousLogger extends PipelineLogger {
    @Override
    public SynchronousLogger clone() {
        return (SynchronousLogger) super.clone();
    }

    @Override
    protected SynchronousLogger newInstance() {
        return new SynchronousLogger();
    }

    @Override
    protected void doLog(LogEntry entry) {
        for (Pipeline pipeline : getPipelines()) {
            boolean rejected = pipeline.getFilters().stream()
                    .anyMatch(filter -> doFilter(filter, entry));
            if (rejected) return;
            Optional<String> formattedEntry = doFormat(pipeline.getFormatter(), entry);
            if (!formattedEntry.isPresent()) return;
            pipeline.getConsumers()
                    .forEach(consumer -> doConsume(consumer, formattedEntry.get(), entry.getLevel()));
        }
    }
}
