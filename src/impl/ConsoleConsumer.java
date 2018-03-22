package it.menzani.logger.impl;

import it.menzani.logger.Level;
import it.menzani.logger.api.Consumer;

public final class ConsoleConsumer implements Consumer {
    @Override
    public void consume(String entry, Level level) {
        switch (level) {
            case FINE:
            case INFORMATION:
            case HEADER:
            case WARNING:
                System.out.println(entry);
                break;
            case FAILURE:
                System.err.println(entry);
                break;
        }
    }
}
