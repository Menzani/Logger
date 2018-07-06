package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.Pipeline;
import it.menzani.logger.api.PipelineLogger;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Stream;

public final class AsynchronousLogger extends PipelineLogger {
    private static final String THREAD_NAME = AsynchronousLogger.class.getSimpleName() + " daemon";

    private volatile int parallelism;
    private volatile ExecutorService executor;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();

    public int getParallelism() {
        return parallelism;
    }

    public AsynchronousLogger withParallelism(int parallelism) {
        this.parallelism = parallelism;
        ThreadManager threadManager = new ThreadManager();
        if (executor == null) {
            Runtime.getRuntime()
                    .addShutdownHook(new Thread(threadManager));
        } else {
            executor.shutdown();
        }
        executor = Executors.newFixedThreadPool(parallelism, threadManager);
        executor.execute(new Consumer());
        return this;
    }

    public AsynchronousLogger withDefaultParallelism() {
        withParallelism(defaultParallelism());
        return this;
    }

    private int defaultParallelism() {
        List<Pipeline> pipelines = getPipelines();
        OptionalInt maxElements = Stream.concat(
                pipelines.stream()
                        .map(Pipeline::getFilters),
                pipelines.stream()
                        .map(Pipeline::getConsumers))
                .mapToInt(Set::size)
                .max();
        return Math.max(pipelines.size(), maxElements.orElse(1));
    }

    @Override
    public AsynchronousLogger setPipelines(Pipeline... pipelines) {
        super.setPipelines(pipelines);
        return this;
    }

    @Override
    public AsynchronousLogger addPipeline(Pipeline pipeline) {
        super.addPipeline(pipeline);
        return this;
    }

    @Override
    public AsynchronousLogger clone() {
        return (AsynchronousLogger) super.clone();
    }

    @Override
    protected void doLog(LogEntry entry) {
        queue.add(entry);
    }

    private final class ThreadManager implements ThreadFactory, Runnable {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName(THREAD_NAME);
            return thread;
        }

        @Override
        public void run() {
            executor.shutdown();
        }
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
