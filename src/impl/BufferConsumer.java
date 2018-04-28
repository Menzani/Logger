package it.menzani.logger.impl;

import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Level;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class BufferConsumer implements Consumer {
    private final BlockingQueue<String> buffer = new LinkedBlockingQueue<>();

    @Override
    public void consume(String entry, Level level) {
        buffer.add(entry);
    }

    public String nextEntry() throws InterruptedException {
        return buffer.take();
    }
}
