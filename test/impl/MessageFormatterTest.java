package it.menzani.logger.impl;

import it.menzani.logger.api.Formatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageFormatterTest {
    private static final String normalized = "Hello" + System.lineSeparator() + "Hello";

    private Formatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new MessageFormatter();
    }

    @Test
    void lineSeparatorNormalization() throws Exception {
        assertEquals(normalized, normalize("Hello\r\nHello"));
        assertEquals(normalized, normalize("Hello\nHello"));
        assertEquals(normalized, normalize("Hello%nHello"));
    }

    private String normalize(String message) throws Exception {
        return formatter.format(new LogEntry(null, message, null));
    }
}