package it.menzani.logger;

import it.menzani.logger.impl.StandardLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineTest {
    private Pipeline pipeline;

    @BeforeEach
    void init() {
        pipeline = new Pipeline();
    }

    @Test
    void isDisabled() {
        pipeline.disable();
        assertTrue(pipeline.isDisabled());
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void getVerbosity(StandardLevel level) {
        pipeline.withVerbosity(level);
        assertEquals(Optional.of(level), pipeline.getVerbosity());
    }

    @Test
    void getVerbosityNoResult() {
        assertEquals(Optional.empty(), pipeline.getVerbosity());
    }

    @Test
    void getVerbosityMultipleValues() {
        pipeline.withVerbosity(StandardLevel.INFORMATION);
        pipeline.withVerbosity(StandardLevel.FINE);
        assertEquals(Optional.of(StandardLevel.INFORMATION), pipeline.getVerbosity());
        pipeline.withVerbosity(StandardLevel.WARNING);
        assertEquals(Optional.of(StandardLevel.WARNING), pipeline.getVerbosity());
    }
}