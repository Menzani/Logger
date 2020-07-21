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

package eu.menzani.logger;

import eu.menzani.logger.api.Level;
import eu.menzani.logger.api.Logger;
import eu.menzani.logger.impl.StandardLevel;

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
