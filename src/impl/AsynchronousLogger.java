package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.PipelineLogger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class AsynchronousLogger extends PipelineLogger {
    private static final String THREAD_NAME = AsynchronousLogger.class.getSimpleName() + " daemon";

    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();

    {
        Thread thread = new Thread(new Consumer(), THREAD_NAME);
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
                if (entry == null) return;
                getPipelines().parallelStream().forEach(pipeline -> {
                    boolean rejected = pipeline.getFilters().parallelStream()
                            .anyMatch(newFilterFunction(entry));
                    if (rejected) return;
                    String formattedEntry = doFormat(pipeline.getFormatter(), entry);
                    if (formattedEntry == null) return;
                    pipeline.getConsumers().parallelStream()
                            .forEach(newConsumerFunction(formattedEntry, entry.getLevel()));
                });
            }
        }

        private LogEntry consumeOne() {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                printLoggerError(THREAD_NAME + " thread was interrupted.");
                e.printStackTrace();
                return null;
            }
        }
    }
}
