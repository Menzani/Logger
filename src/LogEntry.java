package it.menzani.logger;

import it.menzani.logger.api.LazyMessage;
import it.menzani.logger.api.Level;

public final class LogEntry {
    private final Level level;
    private final Object message;
    private final LazyMessage lazyMessage;

    public LogEntry(Level level, Object message, LazyMessage lazyMessage) {
        this.level = level;
        this.message = message;
        this.lazyMessage = lazyMessage;
    }

    public Level getLevel() {
        return level;
    }

    public Object getMessage() throws EvaluationException {
        if (message != null) {
            return message;
        }
        try {
            return lazyMessage.evaluate();
        } catch (Throwable t) {
            throw new EvaluationException(t);
        }
    }
}
