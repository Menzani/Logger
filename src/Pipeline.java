package it.menzani.logger;

import it.menzani.logger.api.Cloneable;
import it.menzani.logger.api.*;
import it.menzani.logger.impl.LevelFilter;
import it.menzani.logger.impl.MessageFormatter;
import it.menzani.logger.impl.RejectAllFilter;
import it.menzani.logger.impl.StandardLevel;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class Pipeline implements Toggleable, Cloneable<Pipeline> {
    private final Set<Filter> filters = new CopyOnWriteArraySet<>();
    private volatile Formatter formatter = new MessageFormatter();
    private final Set<Consumer> consumers = new CopyOnWriteArraySet<>();

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

    public Optional<Level> getVerbosity() {
        return filters.stream()
                .filter(LevelFilter.class::isInstance)
                .map(LevelFilter.class::cast)
                .map(LevelFilter::getLevel)
                .min(new Level.Comparator());
    }

    public boolean isLoggable(Level level) {
        return getVerbosity()
                .map(level::compareTo)
                .map(verbosity -> verbosity != Level.Verbosity.GREATER)
                .orElse(true);
    }

    @Override
    public void disable() {
        addFilter(new RejectAllFilter());
    }

    @Override
    public boolean isDisabled() {
        return filters.stream()
                .anyMatch(RejectAllFilter.class::isInstance);
    }

    @Override
    public Pipeline clone() {
        Pipeline clone = new Pipeline();
        filters.forEach(clone::addFilter);
        clone.setFormatter(formatter);
        consumers.forEach(clone::addConsumer);
        return clone;
    }
}
