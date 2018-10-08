package it.menzani.logger;

import it.menzani.logger.api.Level;
import it.menzani.logger.api.Logger;
import it.menzani.logger.impl.StandardLevel;

import java.time.Duration;

public final class Profiler implements AutoCloseable {
    private final Logger logger;
    private final Level level;
    private final String format;
    private final long startTime;

    private Profiler(Logger logger, Level level, String format) {
        this.logger = logger;
        this.level = level;
        this.format = format;
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
        String formatted = String.format("%ds %dms", duration.getSeconds() % 60, duration.getNano() / 1_000_000);
        return format.replace("{}", formatted);
    }

    @Override
    public void close() {
        report();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements it.menzani.logger.Builder<Profiler> {
        private Logger logger;
        private Level level = StandardLevel.DEBUG;
        private String format;
        private boolean locked;

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

        public Builder withFormat(String format) {
            checkLocked();
            this.format = format;
            return this;
        }

        protected void checkLocked() {
            if (locked) throw it.menzani.logger.Builder.newLockedException();
        }

        @Override
        public void validate() {
            if (format == null) throw it.menzani.logger.Builder.newValidationException("format");
        }

        @Override
        public Profiler build() {
            if (!locked) validate();
            return new Profiler(logger, level, format);
        }

        @Override
        public Builder lock() {
            if (!locked) {
                locked = true;
                validate();
            }
            return this;
        }
    }
}
