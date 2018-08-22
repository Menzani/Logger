package it.menzani.logger.api;

import it.menzani.logger.impl.LogEntry;
import it.menzani.logger.impl.Producer;
import it.menzani.logger.impl.StandardLevel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public abstract class AbstractLogger implements Logger {
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
    public void throwable(Throwable t, LazyMessage lazyMessage) {
        throwable(StandardLevel.FAILURE, t, lazyMessage);
    }

    @Override
    public void throwable(Level level, Throwable t, LazyMessage lazyMessage) {
        log(level, lazyMessage);
        log(level, () -> throwableToString(t));
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
    public void throwable(Throwable t, Object message) {
        throwable(StandardLevel.FAILURE, t, message);
    }

    @Override
    public void throwable(Level level, Throwable t, Object message) {
        log(level, message);
        log(level, throwableToString(t));
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

    static String throwableToString(Throwable t) {
        Writer writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    protected static String doProduce(Producer producer, Map<Formatter, String> formattedFragments) {
        StringBuilder builder = new StringBuilder();
        for (Object fragment : producer.getFragments()) {
            if (fragment instanceof Formatter) {
                builder.append(formattedFragments.get(fragment));
            } else if (fragment instanceof CharSequence) {
                builder.append((CharSequence) fragment);
            } else if (fragment instanceof Character) {
                builder.append((char) fragment);
            } else {
                throw new AssertionError();
            }
        }
        return builder.toString();
    }

    protected enum ReservedLevel implements Level {
        LOGGER(-1, "LOGGER", false);

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

    protected static abstract class Error {
        private final String message;

        protected Error(String message) {
            this.message = message;
        }

        public void print(Exception e) {
            System.err.println("[Logger] " + message);
            e.printStackTrace();
        }
    }

    static final class PipelineError extends Error {
        PipelineError(Class<?> apiClass, Object implObject) {
            super("Could not pass log entry to " + apiClass.getSimpleName() + ": " + implObject.getClass().getName());
            assert apiClass == Filter.class || apiClass == Formatter.class || apiClass == Consumer.class;
        }
    }
}
