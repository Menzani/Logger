package it.menzani.logger.impl;

import it.menzani.logger.api.ExceptionHandler;
import it.menzani.logger.api.LoggerException;

public final class ConsoleExceptionHandler implements ExceptionHandler {
    private final String prefix;

    public ConsoleExceptionHandler() {
        this("[Logger] ");
    }

    public ConsoleExceptionHandler(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void handle(LoggerException exception) {
        synchronized (System.err) {
            System.err.println(prefix + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
