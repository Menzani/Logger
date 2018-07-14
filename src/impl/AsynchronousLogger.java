package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.Pipeline;
import it.menzani.logger.Producer;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.Formatter;
import it.menzani.logger.api.PipelineLogger;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AsynchronousLogger extends PipelineLogger {
    private volatile ExecutorService executor;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();
    private final Object queueMonitor = new Object();

    public AsynchronousLogger() {
        super();
    }

    public AsynchronousLogger(String name) {
        super(name);
    }

    int getParallelism() {
        return ((ThreadPoolExecutor) executor).getCorePoolSize();
    }

    public AsynchronousLogger setParallelism(int parallelism) {
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

    public AsynchronousLogger setDefaultParallelism(boolean log) {
        setDefaultParallelism();
        if (log) {
            log(ReservedLevel.LOGGER, "Parallelism of AsynchronousLogger " +
                    getName().map(name -> '\'' + name + "' ").orElse("") +
                    "set to " + getParallelism());
        }
        return this;
    }

    public AsynchronousLogger setDefaultParallelism() {
        setParallelism(defaultParallelism());
        return this;
    }

    private int defaultParallelism() {
        Set<Pipeline> pipelines = getPipelines();
        OptionalInt maxElements = Stream.concat(
                pipelines.stream()
                        .map(Pipeline::getFilters),
                pipelines.stream()
                        .map(Pipeline::getConsumers))
                .mapToInt(Set::size)
                .max();
        return 1 + pipelines.size() + maxElements.orElse(0);
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
        return new AsynchronousLogger(getName().orElse(null));
    }

    @Override
    protected void doLog(LogEntry entry) {
        queue.add(entry);
    }

    private static <T> Stream<T> joinAll(Stream<Future<T>> futures) throws InterruptedException, ExecutionException {
        Stream.Builder<T> builder = Stream.builder();
        for (Future<T> future : futures.collect(Collectors.toSet())) {
            T result = future.get();
            if (result == null) continue;
            builder.accept(result);
        }
        return builder.build();
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
                Error error = new ThreadInterruptedError();
                error.print();
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
                        queueMonitor.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                Error error = new ThreadInterruptedError();
                error.print();
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
                Future<Boolean> future = completion.submit(() -> filter.test(entry));
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

            Producer producer = pipeline.getProducer();
            Map<Formatter, Optional<String>> formattedElements = joinAll(producer.getFormatters()
                    .map(formatter -> (Callable<Map.Entry<Formatter, Optional<String>>>)
                            () -> new AbstractMap.SimpleImmutableEntry<>(
                                    formatter, formatter.apply(entry, AsynchronousLogger.this)))
                    .map(executor::submit))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            Optional<String> formattedEntry = producer.produce(formattedElements);
            if (!formattedEntry.isPresent()) return;

            joinAll(pipeline.getConsumers().stream()
                    .map(consumer -> (Runnable) () -> consumer.accept(entry, formattedEntry.get()))
                    .map(task -> executor.submit(task)));
        }
    }

    private static final class ThreadInterruptedError extends Error {
        private ThreadInterruptedError() {
            super(Thread.currentThread().getName() + " thread was interrupted.");
        }
    }
}
