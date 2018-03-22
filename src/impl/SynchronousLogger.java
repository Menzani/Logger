package it.menzani.logger.impl;

import it.menzani.logger.Level;
import it.menzani.logger.LogEntry;
import it.menzani.logger.api.AbstractLogger;
import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.LazyMessage;

public final class SynchronousLogger extends AbstractLogger {
    @Override
    public void log(Level level, LazyMessage lazyMessage) {
        String entry;
        try {
            entry = formatter.format(new LogEntry(level, lazyMessage));
        } catch (Exception e) {
            formatterError();
            e.printStackTrace();
            return;
        }
        for (Consumer consumer : consumers) {
            try {
                consumer.consume(entry, level);
            } catch (Exception e) {
                consumerError(consumer);
                e.printStackTrace();
            }
        }
    }
}
