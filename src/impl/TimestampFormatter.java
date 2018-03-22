package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimestampFormatter implements Formatter {
    private final DateTimeFormatter formatter;

    public TimestampFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String format(LogEntry entry) throws Exception {
        String dateTime = formatter.format(LocalDateTime.now());
        Object message = entry.getLazyMessage().evaluate();
        return '[' + dateTime + ' ' + entry.getLevel().getMarker() + "] " + message;
    }
}
