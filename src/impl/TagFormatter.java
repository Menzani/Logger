package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;

public final class TagFormatter extends MessageFormatter {
    private final MessageFormatter messageFormatter;
    private final String tag;

    public TagFormatter(String tag) {
        this(new MessageFormatter(), tag);
    }

    public TagFormatter(MessageFormatter messageFormatter, String tag) {
        this.messageFormatter = messageFormatter;
        this.tag = '[' + tag + "] ";
    }

    @Override
    public String format(LogEntry entry) throws Exception {
        return tag + messageFormatter.format(entry);
    }
}
