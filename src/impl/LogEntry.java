package it.menzani.logger.impl;

import it.menzani.logger.AtomicLazy;
import it.menzani.logger.Lazy;
import it.menzani.logger.api.LazyMessage;
import it.menzani.logger.api.Level;

import java.time.temporal.Temporal;

public final class LogEntry {
    private final Level level;
    private final Object message;
    private final Lazy<Object> lazyMessage;
    private volatile Temporal timestamp;

    public LogEntry(Level level, Object message, LazyMessage lazyMessage) {
        this.level = level;
        this.message = message;
        this.lazyMessage = lazyMessage == null ? null : new AtomicLazy<>(lazyMessage::evaluate, 5);
    }

    public Level getLevel() {
        return level;
    }

    public Object getMessage() throws EvaluationException {
        if (message != null) {
            return message;
        }
        assert lazyMessage != null;
        try {
            return lazyMessage.get();
        } catch (Exception e) {
            throw new EvaluationException(e);
        }
    }

    public Temporal getTimestamp() {
        if (timestamp == null) {
            throw new IllegalStateException("Timestamp unavailable. Please add at least one TimestampFormatter to the current pipeline.");
        }
        return timestamp;
    }

    synchronized void setTimestamp(Temporal timestamp) {
        if (this.timestamp != null) {
            throw new IllegalStateException("Timestamp already set. Please mark all but one TimestampFormatter in the current pipeline not as timestamp sources.");
        }
        this.timestamp = timestamp;
    }
}
