package it.menzani.logger.api;

import it.menzani.logger.LogEntry;

public interface Consumer {
    void consume(LogEntry entry, String formattedEntry) throws Exception;
}
