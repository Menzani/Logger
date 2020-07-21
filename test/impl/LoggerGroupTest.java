/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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