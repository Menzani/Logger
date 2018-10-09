package it.menzani.logger.impl;

import it.menzani.logger.api.LoggerException;

public final class ThreadInterruptedLoggerException extends LoggerException {
    private final Thread thread;

    public ThreadInterruptedLoggerException(InterruptedException exception) {
        this(exception, Thread.currentThread());
    }

    public ThreadInterruptedLoggerException(InterruptedException exception, Thread thread) {
        super(thread.getName() + " thread was interrupted.", exception);
        this.thread = thread;
    }

    public Thread getThread() {
        return thread;
    }

    public void resetInterruptStatus() {
        thread.interrupt();
    }
}
