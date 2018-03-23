package it.menzani.logger.impl;

import it.menzani.logger.Level;
import it.menzani.logger.LogEntry;
import it.menzani.logger.api.AbstractLogger;
import it.menzani.logger.api.LazyMessage;

public final class SynchronousLogger extends AbstractLogger {
    @Override
    public void log(Level level, LazyMessage lazyMessage) {
        String entry = doFormat(new LogEntry(level, lazyMessage));
        if (entry == null) return;
        consumers.forEach(newConsumerFunction(entry, level));
    }
}
