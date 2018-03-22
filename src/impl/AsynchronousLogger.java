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
                LogEntry entry;
                try {
                    entry = queue.take();
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
                String formattedEntry;
                try {
                    formattedEntry = formatter.format(entry);
                } catch (Exception e) {
                    formatterError();
                    e.printStackTrace();
                    continue;
                }
                consumers.parallelStream()
                        .forEach(consumer -> {
                            try {
                                consumer.consume(formattedEntry, entry.getLevel());
                            } catch (Exception e) {
                                consumerError(consumer);
                                e.printStackTrace();
                            }
                        });
            }
        }
    }
}
