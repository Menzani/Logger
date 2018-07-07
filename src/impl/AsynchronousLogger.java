package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.Pipeline;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.PipelineLogger;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AsynchronousLogger extends PipelineLogger {
    private volatile int parallelism;
    private volatile ExecutorService executor;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();
    private final Object queueMonitor = new Object();

    public int getParallelism() {
        return parallelism;
    }

    public AsynchronousLogger withParallelism(int parallelism) {
        this.parallelism = parallelism;
        ThreadManager threadManager = new ThreadManager();
        if (executor == null) {
            Runtime.getRuntime()
                    .addShutdownHook(new Thread(threadManager, "AsynchronousLogger shutdown"));
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
    protected AsynchronousLogger newInstance() {
        return new AsynchronousLogger();
    }

    @Override
    protected void doLog(LogEntry entry) {
        queue.add(entry);
    }

    private static void printThreadingError() {
        printLoggerError(Thread.currentThread().getName() + " thread was interrupted.");
    }

    private static void joinAll(Stream<Future<?>> futures) throws InterruptedException, ExecutionException {
        for (Future<?> future : futures.collect(Collectors.toSet())) {
            future.get();
        }
    }

    private final class ThreadManager implements ThreadFactory, Runnable {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName("AsynchronousLogger daemon");
            thread.setDaemon(true);
            return thread;
        }

        @Override
        public void run() {
            try {
                synchronized (queueMonitor) {
                    while (!queue.isEmpty()) {
                        queueMonitor.wait();
                    }
                }
            } catch (InterruptedException e) {
                printThreadingError();
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        }
    }

    private final class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    LogEntry entry = queue.take();
                    joinAll(getPipelines().stream()
                            .map(pipeline -> new PipelineConsumer(pipeline, entry))
                            .map(executor::submit));
                    synchronized (queueMonitor) {
                        queueMonitor.notify();
                    }
                }
            } catch (InterruptedException e) {
                printThreadingError();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ExecutionException) {
                    cause = cause.getCause();
                }
                cause.printStackTrace();
            }
        }
    }

    private final class PipelineConsumer implements Callable<Void> {
        private final Pipeline pipeline;
        private final LogEntry entry;

        private PipelineConsumer(Pipeline pipeline, LogEntry entry) {
            this.pipeline = pipeline;
            this.entry = entry;
        }

        @Override
        public Void call() throws InterruptedException, ExecutionException {
            run();
            return null;
        }

        private void run() throws InterruptedException, ExecutionException {
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
            } finally {
                for (Future<?> future : futures) {
                    future.cancel(true);
                }
            }

            Optional<String> formattedEntry = doFormat(pipeline.getFormatter(), entry);
            if (!formattedEntry.isPresent()) return;
            joinAll(pipeline.getConsumers().stream()
                    .map(consumer -> (Runnable) () -> doConsume(consumer, formattedEntry.get(), entry.getLevel()))
                    .map(executor::submit));
        }
    }
}
