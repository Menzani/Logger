package it.menzani.logger.api;

import it.menzani.logger.Objects;

public final class CloneException extends RuntimeException {
    public CloneException(Cloneable<?> cloneable) {
        super(createMessage(cloneable));
    }

    public CloneException(Cloneable<?> cloneable, String cause) {
        super(createMessage(cloneable) + ": " + Objects.objectNotNull(cause, "cause") + '.');
    }

    public CloneException(Cloneable<?> cloneable, Throwable cause) {
        super(createMessage(cloneable), Objects.objectNotNull(cause, "cause"));
    }

    private static String createMessage(Cloneable<?> cloneable) {
        return "Could not produce clone of " + Objects.objectNotNull(cloneable, "cloneable").getClass().getName();
    }
}
