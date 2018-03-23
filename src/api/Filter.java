package it.menzani.logger.api;

import it.menzani.logger.LogEntry;

public interface Filter {
    boolean reject(LogEntry entry) throws Exception;
}
