package it.menzani.logger;

import it.menzani.logger.api.Cloneable;

public final class CloneException extends RuntimeException {
    public CloneException(Cloneable<?> cloneable) {
        super("Could not produce clone of " + cloneable.getClass().getName());
    }
}
