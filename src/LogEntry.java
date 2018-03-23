package it.menzani.logger;

import it.menzani.logger.api.LazyMessage;
import it.menzani.logger.api.Level;

public final class LogEntry {
    private final Level level;
    private final LazyMessage lazyMessage;

    public LogEntry(Level level, LazyMessage lazyMessage) {
        this.level = level;
        this.lazyMessage = lazyMessage;
    }

    public Level getLevel() {
        return level;
    }

    public LazyMessage getLazyMessage() {
        return lazyMessage;
    }
}
