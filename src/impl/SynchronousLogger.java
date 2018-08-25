package it.menzani.logger.impl;

import it.menzani.logger.api.Formatter;
import it.menzani.logger.api.PipelineLogger;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SynchronousLogger extends PipelineLogger {
    public SynchronousLogger() {
        super();
    }

    public SynchronousLogger(String name) {
        super(name);
    }

    @Override
    public SynchronousLogger clone() {
        return (SynchronousLogger) super.clone();
    }

    @Override
    protected SynchronousLogger newInstance() {
        return new SynchronousLogger(getName().orElse(null));
    }

    @Override
    protected void doLog(LogEntry logEntry) {
        for (Pipeline pipeline : getPipelines()) {
            boolean rejected = pipeline.getFilters().stream()
                    .anyMatch(filter -> filter.test(logEntry));
            if (rejected) continue;

            ProducerView producer = pipeline.getProducer();
            Map<Formatter, Optional<String>> formattedFragments = producer.getFormatters().stream()
                    .collect(Collectors.toMap(Function.identity(), formatter -> formatter.apply(logEntry, this)));
            if (!formattedFragments.values().stream()
                    .allMatch(Optional::isPresent)) continue;
            String formattedEntry = producer.produce(
                    formattedFragments.entrySet().stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                                Optional<String> formattedFragment = entry.getValue();
                                assert formattedFragment.isPresent();
                                return formattedFragment.get();
                            })));

            pipeline.getConsumers()
                    .forEach(consumer -> consumer.accept(logEntry, formattedEntry));
        }
    }
}
