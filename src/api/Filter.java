package it.menzani.logger.api;

import it.menzani.logger.LogEntry;

public interface Filter {
    boolean reject(LogEntry entry) throws Exception;

    default boolean rejectThrowing(LogEntry entry) {
        try {
            return reject(entry);
        } catch (Exception e) {
            AbstractLogger.Error error = new AbstractLogger.PipelineError(Filter.class, this);
            error.print();
            e.printStackTrace();
            return true;
        }
    }
}
