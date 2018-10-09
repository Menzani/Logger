package it.menzani.logger.impl;

import it.menzani.logger.api.ExceptionHandler;
import it.menzani.logger.api.LoggerException;

public final class ThrowingExceptionHandler implements ExceptionHandler {
    @Override
    public void handle(LoggerException exception) {
        throw exception;
    }
}
