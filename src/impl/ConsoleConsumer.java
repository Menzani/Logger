package it.menzani.logger.impl;

import it.menzani.logger.api.Consumer;

public final class ConsoleConsumer implements Consumer {
    @Override
    public void consume(LogEntry entry, String formattedEntry) {
        if (entry.getLevel().isError()) {
            System.err.println(formattedEntry);
        } else {
            System.out.println(formattedEntry);
        }
    }
}
