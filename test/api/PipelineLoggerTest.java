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

import eu.menzani.logger.impl.Pipeline;
import eu.menzani.logger.impl.SynchronousLogger;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertEquals(pipeline, logger.getPipeline(name));
    }
}