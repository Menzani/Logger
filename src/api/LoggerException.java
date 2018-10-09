package it.menzani.logger.api;

import java.io.PrintStream;
import java.io.PrintWriter;

public abstract class LoggerException extends RuntimeException {
    protected LoggerException(String message, Exception exception) {
        super(message, exception);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        getCause().printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        getCause().printStackTrace(s);
    }
}
