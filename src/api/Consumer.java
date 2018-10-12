package it.menzani.logger.api;

import it.menzani.logger.impl.LogEntry;
import it.menzani.logger.impl.PipelineLoggerException;

public interface Consumer {
    void consume(LogEntry entry, String formattedEntry) throws Exception;

    default void accept(LogEntry entry, String formattedEntry, AbstractLogger logger) {
        try {
            consume(entry, formattedEntry);
        } catch (Exception e) {
            logger.throwException(new PipelineLoggerException(e, PipelineLoggerException.PipelineElement.CONSUMER, this));
        }
    }
}
