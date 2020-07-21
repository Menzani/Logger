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

import eu.menzani.logger.api.Logger;
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