package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.Pipeline;
import it.menzani.logger.api.AbstractLoggerTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsynchronousLoggerTest extends AbstractLoggerTest {
    @Override
    protected AsynchronousLogger newLogger(Pipeline pipeline) {
        return new AsynchronousLogger()
                .addPipeline(pipeline)
                .setDefaultParallelism();
    }

    @ParameterizedTest
    @MethodSource
    void defaultParallelism(Pipeline[] pipelines, int expectedParallelism) {
        AsynchronousLogger logger = new AsynchronousLogger()
                .setPipelines(pipelines)
                .setDefaultParallelism();
        assertEquals(expectedParallelism, logger.getParallelism());
    }

    static Stream<Arguments> defaultParallelism() {
        return Stream.of(
                /* [1] */ Arguments.of(arrayOf(), 1),
                /* [2] */ Arguments.of(arrayOf(new Pipeline()), 3),
                /* [3] */ Arguments.of(arrayOf(new Pipeline().addFilter(Filter.instance)), 3),
                /* [4] */ Arguments.of(arrayOf(new Pipeline().addFilter(Filter.instance).addFilter(Filter.instance)), 4),
                /* [5] */ Arguments.of(arrayOf(new Pipeline().addFilter(Filter.instance).addConsumer(Consumer.instance)), 3),
                /* [6] */ Arguments.of(arrayOf(new Pipeline().addFilter(Filter.instance).addConsumer(Consumer.instance).addConsumer(Consumer.instance)), 4),
                /* [7] */ Arguments.of(arrayOf(new Pipeline(), new Pipeline()), 4),
                /* [8] */ Arguments.of(arrayOf(new Pipeline(), new Pipeline().addFilter(Filter.instance)), 4),
                /* [9] */ Arguments.of(arrayOf(new Pipeline(), new Pipeline().addFilter(Filter.instance).addFilter(Filter.instance)), 5),
                /* [10] */ Arguments.of(arrayOf(new Pipeline(), new Pipeline().addFilter(Filter.instance).addConsumer(Consumer.instance)), 4),
                /* [11] */ Arguments.of(arrayOf(new Pipeline(), new Pipeline().addFilter(Filter.instance).addConsumer(Consumer.instance).addConsumer(Consumer.instance)), 5),
                /* [12] */ Arguments.of(arrayOf(new Pipeline().addConsumer(Consumer.instance), new Pipeline().addFilter(Filter.instance)), 4),
                /* [13] */ Arguments.of(arrayOf(new Pipeline().addFilter(Filter.instance).addFilter(Filter.instance), new Pipeline().addFilter(Filter.instance)), 5),
                /* [14] */ Arguments.of(arrayOf(new Pipeline().addConsumer(Consumer.instance).addConsumer(Consumer.instance), new Pipeline().addConsumer(Consumer.instance)), 5)
        );
    }

    private static Object[] arrayOf(Pipeline... pipelines) {
        return pipelines;
    }

    private static class Filter implements it.menzani.logger.api.Filter {
        private static final Filter instance = new Filter();

        @Override
        public boolean reject(LogEntry entry) {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

    private static class Consumer implements it.menzani.logger.api.Consumer {
        private static final Consumer instance = new Consumer();

        @Override
        public void consume(LogEntry entry, String formattedEntry) {
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}