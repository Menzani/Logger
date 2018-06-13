package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

public final class TimestampFormatter extends LevelFormatter {
    private final LevelFormatter levelFormatter;
    private final Clock clock;
    private final DateTimeFormatter formatter;

    public TimestampFormatter() {
        this(new LevelFormatter());
    }

    public TimestampFormatter(LevelFormatter levelFormatter) {
        this(levelFormatter, LocalDateTime::now, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public TimestampFormatter(LevelFormatter levelFormatter, Clock clock, DateTimeFormatter formatter) {
        this.levelFormatter = levelFormatter;
        this.clock = clock;
        this.formatter = formatter;
    }

    @Override
    public String format(LogEntry entry) throws Exception {
        String now = formatter.format(clock.now());
        return new StringBuilder(levelFormatter.format(entry))
                .insert(1, ' ')
                .insert(1, now)
                .toString();
    }

    @FunctionalInterface
    public interface Clock {
        TemporalAccessor now() throws Exception;
    }
}
