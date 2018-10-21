/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.api.Cloneable;
import it.menzani.logger.api.*;

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
