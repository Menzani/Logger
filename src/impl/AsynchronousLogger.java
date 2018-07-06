package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.Pipeline;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.PipelineLogger;

import java.util.List;
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
        int elementCount = Stream.concat(
                pipelines.stream()
                        .map(Pipeline::getFilters),
                pipelines.stream()
                        .map(Pipeline::getConsumers))
                .mapToInt(Set::size)
                .sum();
        return 1 + pipelines.size() + elementCount;
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

    private static void logInterruption(InterruptedException e) {
        printLoggerError(THREAD_NAME + " thread was interrupted.");
        e.printStackTrace();
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
            try {
                while (true) {
                    LogEntry entry = queue.take();
                    getPipelines().stream()
                            .map(pipeline -> new PipelineConsumer(pipeline, entry))
                            .forEach(executor::execute);
                }
            } catch (InterruptedException e) {
                logInterruption(e);
            }
        }
    }

    private final class PipelineConsumer implements Runnable {
        private final Pipeline pipeline;
        private final LogEntry entry;

        private PipelineConsumer(Pipeline pipeline, LogEntry entry) {
            this.pipeline = pipeline;
            this.entry = entry;
        }

        @Override
        public void run() {
            Set<Filter> filters = pipeline.getFilters();
            CompletionService<Boolean> completion = new ExecutorCompletionService<>(executor);
            Future<?>[] futures = new Future<?>[filters.size()];
            int i = 0;
            for (Filter filter : filters) {
                Future<Boolean> future = completion.submit(() -> doFilter(filter, entry));
                futures[i++] = future;
            }
            assert i == futures.length;
            try {
                for (int j = 0; j < i; j++) {
                    boolean rejected = completion.take().get();
                    if (rejected) return;
                }
            } catch (InterruptedException e) {
                logInterruption(e);
                return;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return;
            } finally {
                for (Future<?> future : futures) {
                    future.cancel(true);
                }
            }

            String formattedEntry = doFormat(pipeline.getFormatter(), entry);
            if (formattedEntry == null) return;
            pipeline.getConsumers().stream()
                    .map(consumer -> (Runnable) () -> doConsume(consumer, formattedEntry, entry.getLevel()))
                    .forEach(executor::execute);
        }
    }
}
