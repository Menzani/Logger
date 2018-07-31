package it.menzani.logger.api;

import it.menzani.logger.impl.LogEntry;

import java.util.function.Predicate;

public interface Filter extends Predicate<LogEntry> {
    boolean reject(LogEntry entry) throws Exception;

    default boolean test(LogEntry entry) {
        try {
            return reject(entry);
        } catch (Exception e) {
            AbstractLogger.Error error = new AbstractLogger.PipelineError(Filter.class, this);
            error.print(e);
            return true;
        }
    }
}
