package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Formatter;

public class MessageFormatter implements Formatter {
    @Override
    public String format(LogEntry entry) throws Exception {
        return entry.getMessage().toString();
    }
}
