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

package eu.menzani.logger.api;

import eu.menzani.logger.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractLoggerTest {
    private BufferConsumer consumer;
    private Logger logger;

    @BeforeEach
    void init() {
        consumer = new BufferConsumer();
        logger = newLogger(new Pipeline()
                .setProducer(new Producer()
                        .append('[')
                        .append(new TimestampFormatter())
                        .append(' ')
                        .append(new LevelFormatter())
                        .append("] ")
                        .append(new MessageFormatter()))
                .addConsumer(consumer));
    }

    protected abstract PipelineLogger newLogger(Pipeline pipeline);

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logMessage(StandardLevel level) throws InterruptedException {
        UUID message = UUID.randomUUID();
        logger.log(level, message);

        String entry = consumer.nextEntry();
        assertTrue(entry.contains(' ' + level.getMarker() + "] "));
        assertTrue(entry.contains("] " + message));
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logLazyMessage(StandardLevel level) throws InterruptedException {
        UUID message = UUID.randomUUID();
        logger.log(level, () -> message);

        String entry = consumer.nextEntry();
        assertTrue(entry.contains(' ' + level.getMarker() + "] "));
        assertTrue(entry.contains("] " + message));
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logLazyMessageThrowingException(StandardLevel level) throws InterruptedException {
        Exception e = new Exception();
        logger.log(level, () -> {
            throw e;
        });

        String entry = consumer.nextEntry();
        final String errorMarker = AbstractLogger.ReservedLevel.ERROR.getMarker();
        assertTrue(entry.contains(' ' + errorMarker + "] "));
        assertTrue(entry.contains("Could not evaluate lazy message at level: " + level.getMarker() + System.lineSeparator()));
        assertTrue(entry.contains(AbstractLogger.throwableToString(e)));
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logThrowableWithMessage(StandardLevel level) throws InterruptedException {
        Exception e = new Exception();
        UUID message = UUID.randomUUID();
        logger.throwable(level, e, message);

        String entry = consumer.nextEntry();
        assertTrue(entry.contains(' ' + level.getMarker() + "] "));
        assertTrue(entry.contains("] " + message + System.lineSeparator()));
        assertTrue(entry.contains(AbstractLogger.throwableToString(e)));
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logThrowableWithLazyMessage(StandardLevel level) throws InterruptedException {
        Exception e = new Exception();
        UUID message = UUID.randomUUID();
        logger.throwable(level, e, () -> message);

        String entry = consumer.nextEntry();
        assertTrue(entry.contains(' ' + level.getMarker() + "] "));
        assertTrue(entry.contains("] " + message + System.lineSeparator()));
        assertTrue(entry.contains(AbstractLogger.throwableToString(e)));
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logThrowableWithParameterizedMessage(StandardLevel level) throws InterruptedException {
        Exception e = new Exception();
        UUID message = UUID.randomUUID();
        logger.throwable(level, e, "{}", message);

        String entry = consumer.nextEntry();
        assertTrue(entry.contains(' ' + level.getMarker() + "] "));
        assertTrue(entry.contains("] " + message + System.lineSeparator()));
        assertTrue(entry.contains(AbstractLogger.throwableToString(e)));
    }
}