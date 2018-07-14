package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.Pipeline;
import it.menzani.logger.Producer;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.Formatter;
import it.menzani.logger.api.PipelineLogger;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
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
        OptionalInt maxElements = Stream.of(
                pipelines.stream()
                        .map(Pipeline::getFilters),
                pipelines.stream()
                        .map(Pipeline::getProducer)
                        .map(Producer::getFormatters),
                pipelines.stream()
                        .map(Pipeline::getConsumers))
                .flatMap(Function.identity())
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
            boolean failure = joinAny(pipeline.getFilters(), filter -> () -> filter.test(entry), Boolean::booleanValue);
            if (failure) return;

            Producer producer = pipeline.getProducer();
            Map<Formatter, String> formattedElements = new HashMap<>();
            failure = joinAny(producer.getFormatters(),
                    formatter -> () -> new AbstractMap.SimpleImmutableEntry<>(
                            formatter, formatter.apply(entry, AsynchronousLogger.this)),
                    entry -> {
                        Optional<String> formattedEntry = entry.getValue();
                        if (!formattedEntry.isPresent()) return true;
                        formattedElements.put(entry.getKey(), formattedEntry.get());
                        return false;
                    });
            if (failure) return;
            String formattedEntry = producer.produce(formattedElements);

            joinAll(pipeline.getConsumers().stream()
                    .map(consumer -> (Runnable) () -> consumer.accept(entry, formattedEntry))
                    .map(executor::submit));
        }

        private <T, V> boolean joinAny(Set<T> elements, Function<T, Callable<V>> callableFactory,
                                       Predicate<V> failureTester) throws InterruptedException, ExecutionException {
            CompletionService<V> completion = new ExecutorCompletionService<>(executor);
            Future<?>[] futures = new Future<?>[elements.size()];
            int i = 0;
            for (T element : elements) {
                assert Filter.class.isInstance(element) || Formatter.class.isInstance(element);
                Future<V> future = completion.submit(callableFactory.apply(element));
                futures[i++] = future;
            }
            assert i == futures.length;
            try {
                for (int j = 0; j < i; j++) {
                    V value = completion.take().get();
                    if (failureTester.test(value)) return true;
                }
            } finally {
                for (Future<?> future : futures) {
                    future.cancel(true);
                }
            }
            return false;
        }
    }

    private static final class ThreadInterruptedError extends Error {
        private ThreadInterruptedError() {
            super(Thread.currentThread().getName() + " thread was interrupted.");
        }
    }
}
