package it.menzani.logger.api;

import it.menzani.logger.impl.LogEntry;

import java.util.Optional;

public abstract class ToggleableNamedLogger extends AbstractLogger {
    private final String name;
    private volatile boolean disabled;

    protected ToggleableNamedLogger() {
        this(null);
    }

    protected ToggleableNamedLogger(String name) {
        this.name = name;
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public void disable() {
        disabled = true;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    protected void tryLog(LogEntry entry) {
        if (disabled) return;
        doLog(entry);
    }

    protected abstract void doLog(LogEntry entry);
}
