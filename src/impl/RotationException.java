package it.menzani.logger.impl;

public final class RotationException extends RuntimeException {
    public RotationException(String cause) {
        super("Could not switch file: " + cause + '.');
    }

    public RotationException(Exception cause) {
        super("Could not switch file.", cause);
    }
}
