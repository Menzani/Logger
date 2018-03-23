package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.AbstractLogger;

public final class SynchronousLogger extends AbstractLogger {
    @Override
    protected void doLog(LogEntry entry) {
        boolean rejected = filters.stream()
                .anyMatch(newFilterFunction(entry));
        if (rejected) return;
        String formattedEntry = doFormat(entry);
        if (formattedEntry == null) return;
        consumers.forEach(newConsumerFunction(formattedEntry, entry.getLevel()));
    }
}
