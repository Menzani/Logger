package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.Level;

public final class LevelFilter implements Filter {
    private final Level level;

    public LevelFilter(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public boolean reject(LogEntry entry) {
        return entry.getLevel().compareTo(level) == Level.Verbosity.GREATER;
    }
}
