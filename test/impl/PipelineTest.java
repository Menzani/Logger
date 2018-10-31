/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {
    private Pipeline pipeline;

    @BeforeEach
    void init() {
        pipeline = new Pipeline();
    }

    @Test
    void isDisabled() {
        assertFalse(pipeline.isDisabled());
        pipeline.disable();
        assertTrue(pipeline.isDisabled());
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void getVerbosity(StandardLevel level) {
        assertEquals(Optional.empty(), pipeline.getVerbosity());
        pipeline.setVerbosity(level);
        assertEquals(Optional.of(level), pipeline.getVerbosity());
    }

    @Test
    void getVerbosityMultipleValues() {
        pipeline.setVerbosity(StandardLevel.INFORMATION);
        pipeline.setVerbosity(StandardLevel.FINE);
        assertEquals(Optional.of(StandardLevel.INFORMATION), pipeline.getVerbosity());
        pipeline.setVerbosity(StandardLevel.WARNING);
        assertEquals(Optional.of(StandardLevel.WARNING), pipeline.getVerbosity());
    }

    @ParameterizedTest
    @EnumSource(StandardLevel.class)
    void isLoggable(StandardLevel level) {
        assertTrue(pipeline.isLoggable(level));
    }

    @Test
    void isLoggable() {
        pipeline.setVerbosity(StandardLevel.INFORMATION);
        assertTrue(pipeline.isLoggable(StandardLevel.INFORMATION));
        assertTrue(pipeline.isLoggable(StandardLevel.FATAL));
        assertFalse(pipeline.isLoggable(StandardLevel.FINE));
        assertFalse(pipeline.isLoggable(StandardLevel.TRACE));
    }
}