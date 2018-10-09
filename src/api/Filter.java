package it.menzani.logger.api;

import it.menzani.logger.impl.LogEntry;
import it.menzani.logger.impl.PipelineLoggerException;

import java.util.function.BiPredicate;

public interface Filter extends BiPredicate<LogEntry, AbstractLogger> {
    boolean reject(LogEntry entry) throws Exception;

    @Override
    default boolean test(LogEntry entry, AbstractLogger logger) {
        try {
            return reject(entry);
        } catch (Exception e) {
            logger.throwException(new PipelineLoggerException(e, Filter.class, this));
            return true;
        }
    }
}
