package it.menzani.logger.impl;

import it.menzani.logger.Pipeline;
import it.menzani.logger.api.AbstractLoggerTest;
import it.menzani.logger.api.PipelineLogger;

class SynchronousLoggerTest extends AbstractLoggerTest {
    @Override
    protected PipelineLogger newLogger(Pipeline pipeline) {
        return new SynchronousLogger()
                .addPipeline(pipeline);
    }
}