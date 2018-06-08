package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;

public class LevelFormatter extends MessageFormatter {
    @Override
    public String format(LogEntry entry) throws Exception {
        return '[' + entry.getLevel().getMarker() + "] " + super.format(entry);
    }
}
