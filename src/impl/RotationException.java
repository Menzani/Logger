package it.menzani.logger.impl;

final class RotationException extends RuntimeException {
    RotationException(String cause) {
        super("Could not switch file: " + cause + '.');
    }

    RotationException(Exception cause) {
        super("Could not switch file.", cause);
    }
}
