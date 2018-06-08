package it.menzani.logger.impl;

import it.menzani.logger.api.AbstractLoggerTest;

class AsynchronousLoggerTest extends AbstractLoggerTest {
    @Override
    protected AsynchronousLogger newLogger() {
        return new AsynchronousLogger();
    }
}