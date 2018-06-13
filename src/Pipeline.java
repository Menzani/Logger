package it.menzani.logger;

import it.menzani.logger.api.*;
import it.menzani.logger.impl.LevelFilter;
import it.menzani.logger.impl.MessageFormatter;
import it.menzani.logger.impl.RejectAllFilter;
import it.menzani.logger.impl.StandardLevel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Pipeline implements Toggleable {
    private final Set<Filter> filters = new HashSet<>();
    private Formatter formatter = new MessageFormatter();
    private final Set<Consumer> consumers = new HashSet<>();

    public Set<Filter> getFilters() {
        return Collections.unmodifiableSet(filters);
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public Set<Consumer> getConsumers() {
        return Collections.unmodifiableSet(consumers);
    }

    public Pipeline setFilters(Filter... filters) {
        this.filters.clear();
        Collections.addAll(this.filters, filters);
        return this;
    }

    public Pipeline setFormatter(Formatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public Pipeline setConsumers(Consumer... consumers) {
        this.consumers.clear();
        Collections.addAll(this.consumers, consumers);
        return this;
    }

    public Pipeline addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public Pipeline addConsumer(Consumer consumer) {
        consumers.add(consumer);
        return this;
    }

    public Pipeline withVerbosity(Level level) {
        addFilter(new LevelFilter(level));
        return this;
    }

    public Pipeline withDefaultVerbosity() {
        withVerbosity(StandardLevel.INFORMATION);
        return this;
    }

    @Override
    public void disable() {
        addFilter(new RejectAllFilter());
    }
}
