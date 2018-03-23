package it.menzani.logger.impl;

import it.menzani.logger.Level;
import it.menzani.logger.LogEntry;
import it.menzani.logger.api.AbstractLogger;
import it.menzani.logger.api.LazyMessage;

import java.util.concurrent.*;

public final class AsynchronousLogger extends AbstractLogger {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        private final ThreadFactory delegate = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = delegate.newThread(r);
            thread.setDaemon(true);
            return thread;
        }
    });
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();

    {
        executor.execute(new Consumer());
    }

    @Override
    public void log(Level level, LazyMessage lazyMessage) {
        queue.add(new LogEntry(level, lazyMessage));
    }

    private final class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                LogEntry entry = consumeOne();
                String formattedEntry = doFormat(entry);
                if (formattedEntry == null) continue;
                consumers.parallelStream()
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
