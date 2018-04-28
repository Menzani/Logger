package it.menzani.logger.api;

import it.menzani.logger.impl.BufferConsumer;
import it.menzani.logger.impl.StandardLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractLoggerTest {
    private BufferConsumer consumer;
    private Logger logger;

    @BeforeEach
    void init() {
        consumer = new BufferConsumer();
        logger = newLogger().setConsumers(Collections.singleton(consumer));
    }

    protected abstract AbstractLogger newLogger();

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logMessage(StandardLevel level) throws InterruptedException {
        UUID message = UUID.randomUUID();
        logger.log(level, message);

        String entry = consumer.nextEntry();
        assertTrue(entry.contains(level.getMarker()));
        assertTrue(entry.contains(message.toString()));
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logLazyMessage(StandardLevel level) throws InterruptedException {
        UUID message = UUID.randomUUID();
        logger.log(level, () -> message);

        String entry = consumer.nextEntry();
        assertTrue(entry.contains(level.getMarker()));
        assertTrue(entry.contains(message.toString()));
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void logLazyMessageThrowingException(StandardLevel level) throws InterruptedException {
        Exception e = new Exception();
        logger.log(level, () -> { throw e; });

        String entry = consumer.nextEntry();
        final String loggerMarker = ReservedLevel.LOGGER.getMarker();
        assertTrue(entry.contains(loggerMarker));
        assertTrue(entry.contains("Could not evaluate lazy message at level: " + level.getMarker()));
        entry = consumer.nextEntry();
        assertTrue(entry.contains(loggerMarker));
        assertTrue(entry.contains(AbstractLogger.throwableToString(e)));
    }
}