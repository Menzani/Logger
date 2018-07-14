package it.menzani.logger.api;

import it.menzani.logger.LogEntry;

public interface Consumer {
    void consume(LogEntry entry, String formattedEntry) throws Exception;

    default void consumeThrowing(LogEntry entry, String formattedEntry) {
        try {
            consume(entry, formattedEntry);
        } catch (Exception e) {
            AbstractLogger.Error error = new AbstractLogger.PipelineError(Consumer.class, this);
            error.print();
            e.printStackTrace();
        }
    }
}
