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

import eu.menzani.logger.Nullable;
import eu.menzani.logger.Objects;
import eu.menzani.logger.api.LazyMessage;

public final class ParameterizedMessage implements LazyMessage {
    private volatile String marker = "{}";
    private final String parameterizedString;
    private final Object[] arguments;

    public ParameterizedMessage(String parameterizedString, @Nullable Object... arguments) {
        this.parameterizedString = Objects.objectNotNull(parameterizedString, "parameterizedString");
        this.arguments = Objects.objectNotNull(arguments, "arguments");
    }

    public ParameterizedMessage marker(String marker) {
        this.marker = Objects.objectNotNull(marker, "marker");
        return this;
    }

    public ParameterizedMessage with(LazyMessage... arguments) {
        Objects.objectNotNull(arguments, "arguments");
        int j = 0;
        for (int i = 0; i < this.arguments.length; i++) {
            if (this.arguments[i] == null) {
                if (j == arguments.length) {
                    throw new IllegalArgumentException("Too few arguments.");
                }
                this.arguments[i] = Objects.elementNotNull(arguments[j++], "arguments");
            }
        }
        if (j != arguments.length) {
            throw new IllegalArgumentException("Too many arguments.");
        }
        return this;
    }

    @Override
    public Object evaluate() throws Exception {
        StringBuilder builder = new StringBuilder(parameterizedString);
        int i = 0, j = 0;
        while ((i = builder.indexOf(marker, i)) != -1) {
            if (j == arguments.length) {
                throw newProductionException("too few arguments");
            }
            Object argument = arguments[j++];
            if (argument == null) {
                throw newProductionException("placeholder argument not set");
            }
            if (argument instanceof LazyMessage) {
                argument = ((LazyMessage) argument).evaluate();
            }
            String replacement = argument.toString();
            builder.replace(i, i + 2, replacement);
            i += replacement.length();
        }
        if (j != arguments.length) {
            throw newProductionException("too many arguments");
        }
        return builder;
    }

    private static RuntimeException newProductionException(String cause) {
        return new IllegalStateException("Could not produce parameterized message: " + cause + '.');
    }
}
