package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.AbstractLogger;
import it.menzani.logger.Pipeline;

public final class SynchronousLogger extends AbstractLogger {
    @Override
    protected void doLog(LogEntry entry) {
        for (Pipeline pipeline : getPipelines()) {
            boolean rejected = pipeline.getFilters().stream()
                    .anyMatch(newFilterFunction(entry));
            if (rejected) return;
            String formattedEntry = doFormat(pipeline.getFormatter(), entry);
            if (formattedEntry == null) return;
            pipeline.getConsumers().forEach(newConsumerFunction(formattedEntry, entry.getLevel()));
        }
    }
}
