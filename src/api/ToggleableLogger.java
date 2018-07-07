package it.menzani.logger.api;

import it.menzani.logger.LogEntry;

public abstract class ToggleableLogger extends AbstractLogger implements Toggleable {
    private volatile boolean disabled;

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
