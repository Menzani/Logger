/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.ConcurrentLazy;
import eu.menzani.logger.Lazy;
import eu.menzani.logger.Objects;
import eu.menzani.logger.api.LazyMessage;
import eu.menzani.logger.api.Level;

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
        this.lazyMessage = lazyMessage == null ? null : new ConcurrentLazy<>(lazyMessage::evaluate);
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
