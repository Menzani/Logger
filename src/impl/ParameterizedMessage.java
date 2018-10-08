package it.menzani.logger.impl;

import it.menzani.logger.api.LazyMessage;

public final class ParameterizedMessage implements LazyMessage {
    private volatile String marker = "{}";
    private final String parameterizedString;
    private final Object[] arguments;

    public ParameterizedMessage(String parameterizedString, Object... arguments) {
        this.parameterizedString = parameterizedString;
        this.arguments = arguments;
    }

    public ParameterizedMessage marker(String marker) {
        this.marker = marker;
        return this;
    }

    public ParameterizedMessage with(LazyMessage... arguments) {
        int j = 0;
        for (int i = 0; i < this.arguments.length; i++) {
            if (this.arguments[i] == null) {
                if (j == arguments.length) {
                    throw new IllegalArgumentException("Too few arguments.");
                }
                this.arguments[i] = arguments[j++];
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
        return builder.toString();
    }

    private static RuntimeException newProductionException(String cause) {
        return new IllegalStateException("Could not produce parameterized message: " + cause + '.');
    }
}
