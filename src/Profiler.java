/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger;

import it.menzani.logger.api.Level;
import it.menzani.logger.api.Logger;
import it.menzani.logger.impl.StandardLevel;

import java.time.Duration;
import java.util.Queue;

public final class Profiler implements AutoCloseable {
    private final Logger logger;
    private final Level level;
    private final StringFormat messageFormat;
    private final long startTime;

    private Profiler(Logger logger, Level level, String messageFormat) {
        this.logger = logger;
        this.level = level;
        this.messageFormat = new StringFormat(Objects.objectNotNull(messageFormat, "messageFormat"));
        startTime = System.nanoTime();
    }

    public Duration stop() {
        long endTime = System.nanoTime();
        long diff = endTime - startTime;
        return Duration.ofNanos(diff);
    }

    public Duration report() {
        Duration duration = stop();
        if (logger == null) {
            System.out.println(toString(duration));
        } else {
            logger.log(level, () -> toString(duration));
        }
        return duration;
    }

    public String toString(Duration duration) {
        String elapsed = String.format("%ds %dms", duration.getSeconds() % 60, duration.getNano() / 1_000_000);
        return messageFormat.fill("elapsed", elapsed).toString();
    }

    @Override
    public void close() {
        report();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractBuilder<Profiler> {
        private Logger logger;
        private Level level = StandardLevel.DEBUG;
        private String messageFormat;

        protected Builder() {
        }

        public Builder withLogger(Logger logger) {
            checkLocked();
            this.logger = logger;
            return this;
        }

        public Builder withLevel(Level level) {
            checkLocked();
            this.level = Objects.objectNotNull(level, "level");
            return this;
        }

        public Builder withMessageFormat(String messageFormat) {
            checkLocked();
            this.messageFormat = messageFormat;
            return this;
        }

        @Override
        protected void validate(Queue<String> missingProperties) {
            if (messageFormat == null) missingProperties.add("messageFormat");
        }

        @Override
        protected Profiler doBuild() {
            return new Profiler(logger, level, messageFormat);
        }
    }
}
