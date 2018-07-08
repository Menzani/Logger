package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
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
