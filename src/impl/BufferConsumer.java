/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.api.Consumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class BufferConsumer implements Consumer {
    private final BlockingQueue<String> buffer = new LinkedBlockingQueue<>();

    @Override
    public void consume(LogEntry entry, String formattedEntry) {
        buffer.add(formattedEntry);
    }

    public String nextEntry() throws InterruptedException {
        return buffer.take();
    }
}
