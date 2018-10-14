package it.menzani.logger.api;

import it.menzani.logger.Objects;

public final class CloneException extends RuntimeException {
    public CloneException(Cloneable<?> cloneable) {
        super("Could not produce clone of " + Objects.objectNotNull(cloneable, "cloneable").getClass().getName());
    }
}
