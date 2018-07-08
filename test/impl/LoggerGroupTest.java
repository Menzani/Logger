package it.menzani.logger.impl;

import it.menzani.logger.api.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoggerGroupTest {
    private LoggerGroup loggerGroup;

    @BeforeEach
    void init() {
        loggerGroup = new LoggerGroup();
    }

    @Test
    void getLogger() {
        String name = UUID.randomUUID().toString();
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> loggerGroup.getLogger(name));
        assertEquals("No logger found with name: " + name, e.getMessage());
        Logger logger = new SynchronousLogger(name);
        loggerGroup.addLogger(logger).addLogger(new SynchronousLogger());
        assertEquals(logger, loggerGroup.getLogger(name));
    }
}