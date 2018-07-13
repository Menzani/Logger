package it.menzani.logger.api;

import it.menzani.logger.EvaluationException;
import it.menzani.logger.LogEntry;
import it.menzani.logger.impl.StandardLevel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Optional;

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

    protected static void printLoggerError(String message) {
        System.err.println("[Logger] " + message);
    }

    protected static boolean doFilter(Filter filter, LogEntry entry) {
        try {
            return filter.reject(entry);
        } catch (Exception e) {
            printPipelineError(Filter.class, filter);
            e.printStackTrace();
            return true;
        }
    }

    protected Optional<String> doFormat(Formatter formatter, LogEntry entry) {
        try {
            return Optional.ofNullable(formatter.format(entry));
        } catch (EvaluationException e) {
            String levelMarker = entry.getLevel().getMarker();
            throwable(ReservedLevel.LOGGER, e.getCause(), "Could not evaluate lazy message at level: " + levelMarker);
        } catch (Exception e) {
            printPipelineError(Formatter.class, formatter);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    protected static void doConsume(Consumer consumer, LogEntry entry, String formattedEntry) {
        try {
            consumer.consume(entry, formattedEntry);
        } catch (Exception e) {
            printPipelineError(Consumer.class, consumer);
            e.printStackTrace();
        }
    }

    private static void printPipelineError(Class<?> apiClass, Object implObject) {
        assert apiClass == Filter.class || apiClass == Formatter.class || apiClass == Consumer.class;
        printLoggerError("Could not pass log entry to " + apiClass.getSimpleName() + ": " + implObject.getClass().getName());
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
}
