package it.menzani.logger;

import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.Formatter;
import it.menzani.logger.api.Level;
import it.menzani.logger.impl.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Pipeline {
    private final Set<Filter> filters = new HashSet<>();
    private Formatter formatter = new IdentityFormatter();
    private final Set<Consumer> consumers = new HashSet<>();

    public Set<Filter> getFilters() {
        return filters;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public Set<Consumer> getConsumers() {
        return consumers;
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

    public Pipeline disable() {
        addFilter(new RejectAllFilter());
        return this;
    }

    public static Pipeline newConsoleLocalPipeline() {
        Pipeline pipeline = new Pipeline();
        pipeline.setFormatter(new TimestampFormatter(LocalDateTime::now, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        pipeline.addConsumer(new ConsoleConsumer());
        return pipeline;
    }
}