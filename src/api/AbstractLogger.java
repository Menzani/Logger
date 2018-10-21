/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.api;

import it.menzani.logger.Objects;
import it.menzani.logger.impl.ConsoleExceptionHandler;
import it.menzani.logger.impl.LogEntry;
import it.menzani.logger.impl.ParameterizedMessage;
import it.menzani.logger.impl.StandardLevel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

public abstract class AbstractLogger implements Logger {
    private volatile Clock clock = LocalDateTime::now;
    private volatile ExceptionHandler exceptionHandler = new ConsoleExceptionHandler();

    protected Temporal getClockTime() {
        return Objects.objectNotNull(clock.now(), "clock#now()");
    }

    public AbstractLogger setClock(Clock clock) {
        this.clock = Objects.objectNotNull(clock, "clock");
        return this;
    }

    public <T extends LoggerException> T throwException(T exception) {
        exceptionHandler.handle(exception);
        return exception;
    }

    public AbstractLogger setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = Objects.objectNotNull(exceptionHandler, "exceptionHandler");
        return this;
    }

    @Override
    public void trace(LazyMessage lazyMessage) {
        log(StandardLevel.TRACE, lazyMessage);
    }

    @Override
    public void debug(LazyMessage lazyMessage) {
        log(StandardLevel.DEBUG, lazyMessage);
    }

    @Override
    public void fine(LazyMessage lazyMessage) {
        log(StandardLevel.FINE, lazyMessage);
    }

    @Override
    public void info(LazyMessage lazyMessage) {
        log(StandardLevel.INFORMATION, lazyMessage);
    }

    @Override
    public void header(LazyMessage lazyMessage) {
        log(StandardLevel.HEADER, lazyMessage);
    }

    @Override
    public void warn(LazyMessage lazyMessage) {
        log(StandardLevel.WARNING, lazyMessage);
    }

    @Override
    public void fail(LazyMessage lazyMessage) {
        log(StandardLevel.FAILURE, lazyMessage);
    }

    @Override
    public void throwable(Throwable throwable, LazyMessage lazyMessage) {
        throwable(StandardLevel.FAILURE, throwable, lazyMessage);
    }

    @Override
    public void throwable(Level level, Throwable throwable, LazyMessage lazyMessage) {
        log(level, new ParameterizedMessage("{}%n{}", null, null).with(lazyMessage, () -> throwableToString(throwable)));
    }

    @Override
    public void fatal(LazyMessage lazyMessage) {
        log(StandardLevel.FATAL, lazyMessage);
    }

    @Override
    public void log(Level level, LazyMessage lazyMessage) {
        tryLog(new LogEntry(level, null, lazyMessage));
    }


    @Override
    public void trace(String parameterizedMessage, Object... arguments) {
        log(StandardLevel.TRACE, parameterizedMessage, arguments);
    }

    @Override
    public void debug(String parameterizedMessage, Object... arguments) {
        log(StandardLevel.DEBUG, parameterizedMessage, arguments);
    }

    @Override
    public void fine(String parameterizedMessage, Object... arguments) {
        log(StandardLevel.FINE, parameterizedMessage, arguments);
    }

    @Override
    public void info(String parameterizedMessage, Object... arguments) {
        log(StandardLevel.INFORMATION, parameterizedMessage, arguments);
    }

    @Override
    public void header(String parameterizedMessage, Object... arguments) {
        log(StandardLevel.HEADER, parameterizedMessage, arguments);
    }

    @Override
    public void warn(String parameterizedMessage, Object... arguments) {
        log(StandardLevel.WARNING, parameterizedMessage, arguments);
    }

    @Override
    public void fail(String parameterizedMessage, Object... arguments) {
        log(StandardLevel.FAILURE, parameterizedMessage, arguments);
    }

    @Override
    public void throwable(Throwable throwable, String parameterizedMessage, Object... arguments) {
        throwable(StandardLevel.FAILURE, throwable, parameterizedMessage, arguments);
    }

    @Override
    public void throwable(Level level, Throwable throwable, String parameterizedMessage, Object... arguments) {
        log(level, new ParameterizedMessage("{}%n{}", null, null).with(
                new ParameterizedMessage(parameterizedMessage, Objects.contentNotNull(arguments, "arguments")),
                () -> throwableToString(throwable)));
    }

    @Override
    public void fatal(String parameterizedMessage, Object... arguments) {
        log(StandardLevel.FATAL, parameterizedMessage, arguments);
    }

    @Override
    public void log(Level level, String parameterizedMessage, Object... arguments) {
        log(level, new ParameterizedMessage(parameterizedMessage, Objects.contentNotNull(arguments, "arguments")));
    }


    @Override
    public void trace(Object message) {
        log(StandardLevel.TRACE, message);
    }

    @Override
    public void debug(Object message) {
        log(StandardLevel.DEBUG, message);
    }

    @Override
    public void fine(Object message) {
        log(StandardLevel.FINE, message);
    }

    @Override
    public void info(Object message) {
        log(StandardLevel.INFORMATION, message);
    }

    @Override
    public void header(Object message) {
        log(StandardLevel.HEADER, message);
    }

    @Override
    public void warn(Object message) {
        log(StandardLevel.WARNING, message);
    }

    @Override
    public void fail(Object message) {
        log(StandardLevel.FAILURE, message);
    }

    @Override
    public void throwable(Throwable throwable, Object message) {
        throwable(StandardLevel.FAILURE, throwable, message);
    }

    @Override
    public void throwable(Level level, Throwable throwable, Object message) {
        log(level, message + "%n" + throwableToString(throwable));
    }

    @Override
    public void fatal(Object message) {
        log(StandardLevel.FATAL, message);
    }

    @Override
    public void log(Level level, Object message) {
        tryLog(new LogEntry(level, message, null));
    }

    protected abstract void tryLog(LogEntry entry);

    @Override
    public abstract AbstractLogger clone();

    static String throwableToString(Throwable throwable) {
        Writer writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    protected enum ReservedLevel implements Level {
        INFORMATION(-1, "LOGGER", false),
        ERROR(-1, "LOGGER", true);

        private final int verbosity;
        private final String marker;
        private final boolean error;

        ReservedLevel(int verbosity, String marker, boolean error) {
            this.verbosity = verbosity;
            this.marker = marker;
            this.error = error;
        }

        @Override
        public int getVerbosity() {
            return verbosity;
        }

        @Override
        public String getMarker() {
            return marker;
        }

        @Override
        public boolean isError() {
            return error;
        }
    }
}
