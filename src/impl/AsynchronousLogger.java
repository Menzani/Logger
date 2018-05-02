package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.AbstractLogger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class AsynchronousLogger extends AbstractLogger {
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();

    {
        Thread thread = new Thread(new Consumer(), getClass().getSimpleName() + " daemon");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void doLog(LogEntry entry) {
        queue.add(entry);
    }

    private final class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                LogEntry entry = consumeOne();
                boolean rejected = getFilters().parallelStream()
                        .anyMatch(newFilterFunction(entry));
                if (rejected) continue;
                String formattedEntry = doFormat(entry);
                if (formattedEntry == null) continue;
                getConsumers().parallelStream()
                        .forEach(newConsumerFunction(formattedEntry, entry.getLevel()));
            }
        }

        private LogEntry consumeOne() {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
    }
}
