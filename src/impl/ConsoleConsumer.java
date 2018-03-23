package it.menzani.logger.impl;

import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Level;

public final class ConsoleConsumer implements Consumer {
    @Override
    public void consume(String entry, Level level) {
        if (!(level instanceof StandardLevel)) {
            return;
        }
        switch ((StandardLevel) level) {
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
