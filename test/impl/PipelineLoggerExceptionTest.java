package it.menzani.logger.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PipelineLoggerExceptionTest {
    @ParameterizedTest
    @EnumSource(PipelineLoggerException.PipelineElement.class)
    void badImplObject(PipelineLoggerException.PipelineElement pipelineElement) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new PipelineLoggerException(new Exception(), pipelineElement, new Object()));
        assertEquals("implObject must be a " + pipelineElement + '.', e.getMessage());
    }
}