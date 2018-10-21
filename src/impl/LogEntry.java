/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.AtomicLazy;
import it.menzani.logger.Lazy;
import it.menzani.logger.Objects;
import it.menzani.logger.api.LazyMessage;
import it.menzani.logger.api.Level;

import java.time.temporal.Temporal;
import java.util.concurrent.atomic.AtomicReference;

public final class LogEntry {
    private final Level level;
    private final Object message;
    private final Lazy<Object> lazyMessage;
    private final AtomicReference<Temporal> timestamp = new AtomicReference<>();

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
        Temporal timestamp = this.timestamp.get();
        if (timestamp == null) {
            throw new IllegalStateException("Timestamp was not set.");
        }
        return timestamp;
    }

    public void setTimestamp(Temporal timestamp) {
        boolean updated = this.timestamp.compareAndSet(null, Objects.objectNotNull(timestamp, "timestamp"));
        if (!updated) {
            throw new IllegalStateException("Timestamp already set.");
        }
    }
}
