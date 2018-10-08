package it.menzani.logger.impl;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterizedMessageTest {
    private ParameterizedMessage message;

    @Test
    void oneArgument() throws Exception {
        UUID arg = UUID.randomUUID();
        message = new ParameterizedMessage("Hello {}!", arg);
        assertEquals("Hello " + arg + '!', message.evaluate().toString());
    }

    @Test
    void twoArguments() throws Exception {
        UUID arg0 = UUID.randomUUID(), arg1 = UUID.randomUUID();
        message = new ParameterizedMessage("Hello {} {}!", arg0, arg1);
        assertEquals("Hello " + arg0 + ' ' + arg1 + '!', message.evaluate().toString());
    }

    @Test
    void tooFewArguments() throws Exception {
        UUID arg = UUID.randomUUID();
        message = new ParameterizedMessage("Hello {} {}!", arg);
        IllegalStateException e = assertThrows(IllegalStateException.class, message::evaluate);
        assertEquals("Could not produce parameterized message: too few arguments.", e.getMessage());
    }

    @Test
    void tooManyArguments() throws Exception {
        UUID arg0 = UUID.randomUUID(), arg1 = UUID.randomUUID();
        message = new ParameterizedMessage("Hello {}!", arg0, arg1);
        IllegalStateException e = assertThrows(IllegalStateException.class, message::evaluate);
        assertEquals("Could not produce parameterized message: too many arguments.", e.getMessage());
    }

    @Test
    void placeholderArgument() throws Exception {
        message = new ParameterizedMessage("Hello {}!", (Object) null);
        IllegalStateException e = assertThrows(IllegalStateException.class, message::evaluate);
        assertEquals("Could not produce parameterized message: placeholder argument not set.", e.getMessage());
    }

    @Test
    void oneLazyArgument() throws Exception {
        UUID arg = UUID.randomUUID();
        message = new ParameterizedMessage("Hello {}!", (Object) null).with(() -> arg);
        assertEquals("Hello " + arg + '!', message.evaluate().toString());
    }

    @Test
    void twoLazyArguments() throws Exception {
        UUID arg0 = UUID.randomUUID(), arg1 = UUID.randomUUID();
        message = new ParameterizedMessage("Hello {} {}!", null, null).with(() -> arg0, () -> arg1);
        assertEquals("Hello " + arg0 + ' ' + arg1 + '!', message.evaluate().toString());
    }

    @Test
    void tooFewLazyArguments() {
        UUID arg = UUID.randomUUID();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new ParameterizedMessage("Hello {} {}!", null, null).with(() -> arg));
        assertEquals("Too few arguments.", e.getMessage());
    }

    @Test
    void tooManyLazyArguments() {
        UUID arg0 = UUID.randomUUID(), arg1 = UUID.randomUUID();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new ParameterizedMessage("Hello {}!", (Object) null).with(() -> arg0, () -> arg1));
        assertEquals("Too many arguments.", e.getMessage());
    }
}