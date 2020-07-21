/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.Formatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageFormatterTest {
    private Formatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new MessageFormatter();
    }

    @Test
    void lineSeparatorNormalization() throws Exception {
        String normalized = "Hello" + System.lineSeparator() + "Hello";
        assertEquals(normalized, normalize("Hello\r\nHello"));
        assertEquals(normalized, normalize("Hello\nHello"));
        assertEquals(normalized, normalize("Hello%nHello"));
    }

    private String normalize(String message) throws Exception {
        return formatter.format(new LogEntry(null, message, null));
    }
}