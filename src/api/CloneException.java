package it.menzani.logger.api;

public final class CloneException extends RuntimeException {
    public CloneException(Cloneable<?> cloneable) {
        super("Could not produce clone of " + cloneable.getClass().getName());
    }
}
