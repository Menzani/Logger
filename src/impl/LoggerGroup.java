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

import eu.menzani.logger.api.*;
import eu.menzani.logger.Cloneable;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class LoggerGroup extends ToggleableNamedLogger {
    private final Set<Logger> loggers = new CopyOnWriteArraySet<>();

    public LoggerGroup() {
        super();
    }

    public LoggerGroup(String name) {
        super(name);
    }

    public Set<Logger> getLoggers() {
        return Collections.unmodifiableSet(loggers);
    }

    public Logger getLogger(String name) {
        return loggers.stream()
                .filter(logger -> logger.getName().equals(Optional.of(name))) // Implicit null check of `name`
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No logger found with name: " + name));
    }

    public LoggerGroup setLoggers(Logger... loggers) {
        this.loggers.clear();
        Collections.addAll(this.loggers, loggers);
        return this;
    }

    public LoggerGroup addLogger(Logger logger) {
        loggers.add(logger);
        return this;
    }

    @Override
    public AbstractLogger setClock(Clock clock) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractLogger setExceptionHandler(ExceptionHandler exceptionHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log(Level level, LazyMessage lazyMessage) {
        if (isDisabled()) return;
        for (Logger logger : loggers) {
            logger.log(level, lazyMessage);
        }
    }

    @Override
    public void log(Level level, Object message) {
        if (isDisabled()) return;
        for (Logger logger : loggers) {
            logger.log(level, message);
        }
    }

    @Override
    public LoggerGroup clone() {
        LoggerGroup clone = new LoggerGroup(getName().orElse(null));
        loggers.stream()
                .map(Cloneable::clone)
                .forEach(clone::addLogger);
        return clone;
    }

    @Override
    protected void doLog(LogEntry entry) {
        throw new AssertionError();
    }
}
