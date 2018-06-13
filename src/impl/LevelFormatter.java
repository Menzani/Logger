package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Formatter;

public class LevelFormatter implements Formatter {
    private final MessageFormatter messageFormatter;

    public LevelFormatter() {
        this(new MessageFormatter());
    }

    public LevelFormatter(MessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }

    @Override
    public String format(LogEntry entry) throws Exception {
        return '[' + entry.getLevel().getMarker() + "] " + messageFormatter.format(entry);
    }
}
