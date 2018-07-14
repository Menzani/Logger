package it.menzani.logger.impl;

import it.menzani.logger.EvaluationException;
import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Formatter;

public final class MessageFormatter implements Formatter {
    @Override
    public String format(LogEntry entry) throws EvaluationException {
        return entry.getMessage().toString();
    }
}
