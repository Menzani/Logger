package it.menzani.logger.impl;

import it.menzani.logger.api.Formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageFormatter implements Formatter {
    private static final Pattern markers = Pattern.compile("%n|\\r?\\n");
    private static final String lineSeparator = System.lineSeparator();

    @Override
    public String format(LogEntry entry) throws EvaluationException {
        Matcher matcher = markers.matcher(entry.getMessage().toString());
        return matcher.replaceAll(lineSeparator);
    }
}
