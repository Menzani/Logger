/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.Objects;
import eu.menzani.logger.api.LoggerException;

public final class ThreadInterruptedLoggerException extends LoggerException {
    private final Thread thread;

    public ThreadInterruptedLoggerException(InterruptedException exception) {
        this(exception, Thread.currentThread());
    }

    public ThreadInterruptedLoggerException(InterruptedException exception, Thread thread) {
        super(Objects.objectNotNull(thread, "thread").getName() + " thread was interrupted.", exception);
        this.thread = thread;
    }

    public Thread getThread() {
        return thread;
    }

    public void resetInterruptStatus() {
        thread.interrupt();
    }
}
