package it.menzani.logger.impl;

import it.menzani.logger.api.AbstractLogger;
import it.menzani.logger.api.AbstractLoggerTest;

class SynchronousLoggerTest extends AbstractLoggerTest {
    @Override
    protected AbstractLogger newLogger() {
        return new SynchronousLogger();
    }
}