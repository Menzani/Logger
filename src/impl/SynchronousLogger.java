package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.AbstractLogger;
import it.menzani.logger.api.LazyMessage;
import it.menzani.logger.api.Level;

public final class SynchronousLogger extends AbstractLogger {
    @Override
    public void log(Level level, LazyMessage lazyMessage) {
        LogEntry entry = new LogEntry(level, lazyMessage);
        boolean rejected = filters.stream()
                .anyMatch(newFilterFunction(entry));
        if (rejected) return;
        String formattedEntry = doFormat(entry);
        if (formattedEntry == null) return;
        consumers.forEach(newConsumerFunction(formattedEntry, level));
    }
}
