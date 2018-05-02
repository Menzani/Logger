package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Formatter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class TimestampFormatter implements Formatter {
    private final Clock clock;
    private final DateTimeFormatter formatter;

    public TimestampFormatter(Clock clock, DateTimeFormatter formatter) {
        this.clock = clock;
        this.formatter = formatter;
    }

    @Override
    public String format(LogEntry entry) throws Exception {
        String now = formatter.format(clock.now());
        return '[' + now + ' ' + entry.getLevel().getMarker() + "] " + entry.getMessage();
    }

    @FunctionalInterface
    public interface Clock {
        TemporalAccessor now() throws Exception;
    }
}
