/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.AbstractLoggerTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ParallelLoggerTest extends AbstractLoggerTest {
    @Override
    protected ParallelLogger newLogger(Pipeline pipeline) {
        return new ParallelLogger()
                .addPipeline(pipeline)
                .setDefaultParallelism();
    }

    @ParameterizedTest
    @MethodSource
    void defaultParallelism(Pipeline[] pipelines, int expectedParallelism) {
        ParallelLogger logger = new ParallelLogger()
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

    private static class Filter implements eu.menzani.logger.api.Filter {
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

    private static class Consumer implements eu.menzani.logger.api.Consumer {
        private static final Consumer instance = new Consumer();

        @Override
        public void consume(LogEntry entry, String formattedEntry) {
            // Do nothing
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}