package it.menzani.logger.impl;

import it.menzani.logger.Level;
import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Filter;

public final class LevelFilter implements Filter {
    private final int verbosityThreshold;

    public LevelFilter(Level level) {
        verbosityThreshold = level.getVerbosity();
    }

    @Override
    public boolean reject(LogEntry entry) {
        return entry.getLevel().getVerbosity() > verbosityThreshold;
    }
}
