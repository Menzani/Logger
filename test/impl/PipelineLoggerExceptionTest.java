/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class PipelineLoggerExceptionTest {
    @ParameterizedTest
    @EnumSource(PipelineLoggerException.PipelineElement.class)
    void badImplObject(PipelineLoggerException.PipelineElement pipelineElement) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new PipelineLoggerException(new Exception(), pipelineElement, new Object()));
        assertEquals("implObject must be a " + pipelineElement + '.', e.getMessage());
    }
}