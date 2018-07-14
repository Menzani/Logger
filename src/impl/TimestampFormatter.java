package it.menzani.logger.impl;

import it.menzani.logger.api.Formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

public final class TimestampFormatter implements Formatter {
    private final Clock clock;
    private final DateTimeFormatter formatter;

    public TimestampFormatter() {
        this(LocalDateTime::now, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public TimestampFormatter(Clock clock, DateTimeFormatter formatter) {
        this.clock = clock;
        this.formatter = formatter;
    }

    @Override
    public String format(LogEntry entry) throws Exception {
        return formatter.format(clock.now());
    }

    @FunctionalInterface
    public interface Clock {
        TemporalAccessor now() throws Exception;
    }
}
