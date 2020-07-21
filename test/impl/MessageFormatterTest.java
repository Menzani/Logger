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

import eu.menzani.logger.api.Formatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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