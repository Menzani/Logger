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
    void tooFewArguments() {
        UUID arg = UUID.randomUUID();
        message = new ParameterizedMessage("Hello {} {}!", arg);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> message.evaluate());
        assertEquals("Could not produce parameterized message: too few arguments.", e.getMessage());
    }

    @Test
    void tooManyArguments() {
        UUID arg0 = UUID.randomUUID(), arg1 = UUID.randomUUID();
        message = new ParameterizedMessage("Hello {}!", arg0, arg1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> message.evaluate());
        assertEquals("Could not produce parameterized message: too many arguments.", e.getMessage());
    }

    @Test
    void placeholderArgument() {
        message = new ParameterizedMessage("Hello {}!", (Object) null);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> message.evaluate());
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