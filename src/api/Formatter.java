package it.menzani.logger.api;

import it.menzani.logger.LogEntry;

public interface Formatter {
    String format(LogEntry entry) throws Exception;
}
