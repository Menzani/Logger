package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Cloneable;
import it.menzani.logger.api.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LoggerGroup extends ToggleableLogger {
    private final List<Logger> loggers = new CopyOnWriteArrayList<>();

    public List<Logger> getLoggers() {
        return Collections.unmodifiableList(loggers);
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
        LoggerGroup clone = new LoggerGroup();
        loggers.stream()
                .map(Cloneable::clone)
                .forEach(clone::addLogger);
        return clone;
    }

    @Override
    protected void doLog(LogEntry entry) {
    }
}
