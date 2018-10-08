package it.menzani.logger.impl;

import it.menzani.logger.api.Formatter;

public final class MessageFormatter implements Formatter {
    private static final String lineSeparator = System.lineSeparator();

    @Override
    public String format(LogEntry entry) throws EvaluationException {
        String message = entry.getMessage().toString();
        return message.replaceAll("%n|\\r?\\n", lineSeparator);
    }
}
