package it.menzani.logger.impl;

import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Level;

public final class ConsoleConsumer implements Consumer {
    @Override
    public void consume(String entry, Level level) {
        if (level.isError()) {
            System.err.println(entry);
        } else {
            System.out.println(entry);
        }
    }
}
