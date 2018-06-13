package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

public class TimestampFormatter extends LevelFormatter {
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
        String now = formatter.format(clock.now());
        return new StringBuilder(super.format(entry))
                .insert(1, ' ')
                .insert(1, now)
                .toString();
    }

    @FunctionalInterface
    public interface Clock {
        TemporalAccessor now() throws Exception;
    }
}
