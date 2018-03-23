package it.menzani.logger.api;

import it.menzani.logger.Level;
import it.menzani.logger.LogEntry;
import it.menzani.logger.impl.ConsoleConsumer;
import it.menzani.logger.impl.LevelFilter;
import it.menzani.logger.impl.TimestampFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public abstract class AbstractLogger implements Logger {
    protected Set<Filter> filters = new HashSet<>();
    private Formatter formatter = new TimestampFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    protected Set<Consumer> consumers = new HashSet<>();

    {
        addConsumer(new ConsoleConsumer());
    }

    public AbstractLogger withVerbosity(Level level) {
        return addFilter(new LevelFilter(level));
    }

    public AbstractLogger withDefaultVerbosity() {
        return withVerbosity(Level.INFORMATION);
    }

    public AbstractLogger disable() {
        return addFilter(entry -> true);
    }

    public AbstractLogger setFilters(Set<Filter> filters) {
        this.filters = filters;
        return this;
    }

    public AbstractLogger setFormatter(Formatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public AbstractLogger setConsumers(Set<Consumer> consumers) {
        this.consumers = consumers;
        return this;
    }

    public AbstractLogger addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public AbstractLogger addConsumer(Consumer consumer) {
        consumers.add(consumer);
        return this;
    }

    @Override
    public void fine(LazyMessage lazyMessage) {
        log(Level.FINE, lazyMessage);
    }

    @Override
    public void info(LazyMessage lazyMessage) {
        log(Level.INFORMATION, lazyMessage);
    }

    @Override
    public void header(LazyMessage lazyMessage) {
        log(Level.HEADER, lazyMessage);
    }

    @Override
    public void warn(LazyMessage lazyMessage) {
        log(Level.WARNING, lazyMessage);
    }

    @Override
    public void fail(LazyMessage lazyMessage) {
        log(Level.FAILURE, lazyMessage);
    }

    @Override
    public void throwable(Throwable t, LazyMessage lazyMessage) {
        fail(lazyMessage);
        fail(() -> {
            Writer writer = new StringWriter();
            t.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        });
    }

    protected static Predicate<Filter> newFilterFunction(LogEntry entry) {
        return filter -> {
            try {
                return filter.reject(entry);
            } catch (Exception e) {
                loggerError(Filter.class, filter);
                e.printStackTrace();
                return true;
            }
        };
    }

    protected String doFormat(LogEntry entry) {
        try {
            return formatter.format(entry);
        } catch (Exception e) {
            loggerError(Formatter.class, formatter);
            e.printStackTrace();
            return null;
        }
    }

    protected static java.util.function.Consumer<Consumer> newConsumerFunction(String entry, Level level) {
        return consumer -> {
            try {
                consumer.consume(entry, level);
            } catch (Exception e) {
                loggerError(Consumer.class, consumer);
                e.printStackTrace();
            }
        };
    }

    private static void loggerError(Class<?> apiClass, Object implObject) {
        System.err.println("[Logger] Could not pass log entry to " + apiClass.getSimpleName() + ": " + implObject.getClass().getName());
    }
}
