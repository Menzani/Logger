package it.menzani.logger.impl;

import it.menzani.logger.api.Filter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PipelineErrorTest {
    @Test
    void badApiClass() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new PipelineLoggerException(new Exception(), Object.class, new RejectAllFilter()));
        assertEquals("apiClass must refer to a Filter, Formatter, or Consumer.", e.getMessage());
    }

    @Test
    void badImplObject() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new PipelineLoggerException(new Exception(), Filter.class, new Object()));
        assertEquals("implObject must be a Filter, Formatter, or Consumer.", e.getMessage());
    }
}