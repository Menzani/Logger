package it.menzani.logger.impl;

import it.menzani.logger.api.AbstractLoggerTest;

class SynchronousLoggerTest extends AbstractLoggerTest {
    @Override
    protected SynchronousLogger newLogger() {
        return new SynchronousLogger();
    }
}