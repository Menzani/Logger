package it.menzani.logger.impl;

import it.menzani.logger.Profiler;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.Formatter;
import it.menzani.logger.api.PipelineLogger;
import it.menzani.logger.api.ProfiledLogger;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AsynchronousLogger extends PipelineLogger {
    private volatile ExecutorService executor;
    private volatile ProfiledLogger.ProfilerBuilder profilerBuilder;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();

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
        Consumer consumer = profilerBuilder == null ? new Consumer() : new ProfiledConsumer();
        ThreadManager threadManager = new ThreadManager(consumer);
        if (executor == null) {
            Runtime.getRuntime()
                    .addShutdownHook(new Thread(threadManager, "AsynchronousLogger shutdown"));
        } else {
            executor.shutdown();
        }
        executor = Executors.newFixedThreadPool(parallelism, threadManager);
        executor.execute(consumer);
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
        OptionalInt maxComponents = Stream.of(
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
        return 1 + pipelines.size() + maxComponents.orElse(0);
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
    public AsynchronousLogger profiled() {
        super.profiled();
        return this;
    }

    @Override
    public AsynchronousLogger profiled(ProfiledLogger.ProfilerBuilder profilerBuilder) {
        this.profilerBuilder = profilerBuilder.withLogger(this).lock();
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
        private final Consumer consumer;

        private ThreadManager(Consumer consumer) {
            this.consumer = consumer;
        }

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
                consumer.awaitTermination();
            } catch (InterruptedException e) {
                Error error = new ThreadInterruptedError();
                error.print(e);
            } finally {
                executor.shutdown();
            }
        }
    }

    private class Consumer implements Runnable {
        private volatile boolean running = true;
        private final CountDownLatch terminationLatch = new CountDownLatch(1);

        @Override
        public void run() {
            try {
                while (running || !queue.isEmpty()) {
                    LogEntry entry = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (entry == null) continue;
                    consume(entry);
                }
            } catch (InterruptedException e) {
                Error error = new ThreadInterruptedError();
                error.print(e);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ExecutionException) {
                    cause = cause.getCause();
                }
                cause.printStackTrace();
            } finally {
                terminationLatch.countDown();
            }
        }

        protected void consume(LogEntry entry) throws InterruptedException, ExecutionException {
            joinAll(getPipelines().stream()
                    .map(pipeline -> new PipelineConsumer(pipeline, entry))
                    .map(executor::submit));
        }

        private void awaitTermination() throws InterruptedException {
            running = false;
            terminationLatch.await();
        }
    }

    private final class ProfiledConsumer extends Consumer {
        @Override
        protected void consume(LogEntry entry) throws InterruptedException, ExecutionException {
            try (Profiler ignored = profilerBuilder.build()) {
                super.consume(entry);
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
            Map<Formatter, String> formattedFragments = new HashMap<>();
            failure = joinAny(producer.getFormatters(),
                    formatter -> () -> new AbstractMap.SimpleImmutableEntry<>(
                            formatter, formatter.apply(entry, AsynchronousLogger.this)),
                    entry -> {
                        Optional<String> formattedFragment = entry.getValue();
                        if (!formattedFragment.isPresent()) return true;
                        formattedFragments.put(entry.getKey(), formattedFragment.get());
                        return false;
                    });
            if (failure) return;
            String formattedEntry = producer.produce(formattedFragments);

            joinAll(pipeline.getConsumers().stream()
                    .map(consumer -> (Runnable) () -> consumer.accept(entry, formattedEntry))
                    .map(executor::submit));
        }

        private <T, V> boolean joinAny(Set<T> components, Function<T, Callable<V>> callableFactory,
                                       Predicate<V> failureTester) throws InterruptedException, ExecutionException {
            CompletionService<V> completion = new ExecutorCompletionService<>(executor);
            Future<?>[] futures = new Future<?>[components.size()];
            int i = 0;
            for (T component : components) {
                assert component instanceof Filter || component instanceof Formatter;
                Future<V> future = completion.submit(callableFactory.apply(component));
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
