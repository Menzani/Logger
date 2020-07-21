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