package it.menzani.logger.impl;

import it.menzani.logger.Pipeline;
import it.menzani.logger.api.AbstractLoggerTest;

class AsynchronousLoggerTest extends AbstractLoggerTest {
    @Override
    protected AsynchronousLogger newLogger(Pipeline pipeline) {
        return new AsynchronousLogger()
                .addPipeline(pipeline)
                .withDefaultParallelism();
    }
}