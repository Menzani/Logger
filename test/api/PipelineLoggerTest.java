/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.api;

import it.menzani.logger.impl.Pipeline;
import it.menzani.logger.impl.SynchronousLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PipelineLoggerTest {
    private PipelineLogger logger;

    @BeforeEach
    void init() {
        logger = new SynchronousLogger();
    }

    @Test
    void getPipeline() {
        String name = UUID.randomUUID().toString();
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> logger.getPipeline(name));
        assertEquals("No pipeline found with name: " + name, e.getMessage());
        Pipeline pipeline = new Pipeline(name);
        logger.addPipeline(pipeline).addPipeline(new Pipeline());
        assertEquals(pipeline, logger.getPipeline(name));
    }
}