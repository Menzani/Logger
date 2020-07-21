/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.PipelineLogger;
import eu.menzani.logger.api.Clock;
import eu.menzani.logger.api.ExceptionHandler;
import eu.menzani.logger.api.Formatter;

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
    public SynchronousLogger setClock(Clock clock) {
        super.setClock(clock);
        return this;
    }

    @Override
    public SynchronousLogger setExceptionHandler(ExceptionHandler exceptionHandler) {
        super.setExceptionHandler(exceptionHandler);
        return this;
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
        logEntry.setTimestamp(getClockTime());
        for (Pipeline pipeline : getPipelines()) {
            boolean rejected = pipeline.getFilters().stream()
                    .anyMatch(filter -> filter.test(logEntry, this));
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
                    .forEach(consumer -> consumer.accept(logEntry, formattedEntry, this));
        }
    }
}
