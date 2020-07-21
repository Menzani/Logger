/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.api;

import eu.menzani.logger.impl.LogEntry;

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
