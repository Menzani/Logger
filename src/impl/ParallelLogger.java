/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.Builder;
import eu.menzani.logger.ConfigurableThreadFactory;
import eu.menzani.logger.Profiler;
import eu.menzani.logger.api.Formatter;
import eu.menzani.logger.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ParallelLogger extends PipelineLogger {
    private volatile ExecutorService executor;
    private ShutdownThread shutdownThread;
    private volatile Builder<Profiler> profilerBuilder;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();

    public ParallelLogger() {
        super();
    }

    public ParallelLogger(String name) {
        super(name);
    }

    int getParallelism() {
        return ((ThreadPoolExecutor) executor).getCorePoolSize();
    }

    public synchronized ParallelLogger setParallelism(int parallelism) {
        Runtime runtime = Runtime.getRuntime();
        if (executor != null) {
            shutdownThread.run();
            boolean wasRegistered = runtime.removeShutdownHook(shutdownThread);
            assert wasRegistered;
        }
        Consumer consumer = profilerBuilder == null ? new Consumer() : new ProfiledConsumer(profilerBuilder);
        shutdownThread = new ShutdownThread(consumer);
        runtime.addShutdownHook(shutdownThread);
        executor = Executors.newFixedThreadPool(parallelism, ConfigurableThreadFactory.daemon("ParallelLogger daemon"));
        executor.execute(consumer);
        return this;
    }

    public ParallelLogger setDefaultParallelism(boolean log) {
        int parallelism;
        synchronized (this) {
            setDefaultParallelism();
            parallelism = getParallelism();
        }
        if (log) log(ReservedLevel.INFORMATION, "Parallelism of {} set to {}",
                getName().orElse("ParallelLogger"), parallelism);
        return this;
    }

    public ParallelLogger setDefaultParallelism() {
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
                        .map(ProducerView::getFormatters),
                pipelines.stream()
                        .map(Pipeline::getConsumers))
                .flatMap(stream -> stream) // Function.identity() won't work.
                .mapToInt(Set::size)
                .max();
        return 1 + pipelines.size() + maxComponents.orElse(0);
    }

    @Override
    public ParallelLogger setClock(Clock clock) {
        super.setClock(clock);
        return this;
    }

    @Override
    public ParallelLogger setExceptionHandler(ExceptionHandler exceptionHandler) {
        super.setExceptionHandler(exceptionHandler);
        return this;
    }

    @Override
    public ParallelLogger setPipelines(Pipeline... pipelines) {
        super.setPipelines(pipelines);
        return this;
    }

    @Override
    public ParallelLogger addPipeline(Pipeline pipeline) {
        super.addPipeline(pipeline);
        return this;
    }

    @Override
    public ParallelLogger profiled() {
        super.profiled();
        return this;
    }

    @Override
    public ParallelLogger profiled(ProfiledLogger.ProfilerBuilder profilerBuilder) {
        this.profilerBuilder = profilerBuilder.withLogger(this).lock();
        return this;
    }

    @Override
    public ParallelLogger clone() {
        return (ParallelLogger) super.clone();
    }

    @Override
    protected ParallelLogger newInstance() {
        return new ParallelLogger(getName().orElse(null));
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

    private final class ShutdownThread extends Thread {
        private final Consumer consumer;

        private ShutdownThread(Consumer consumer) {
            super("ParallelLogger shutdown");
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try {
                consumer.awaitTermination();
            } catch (InterruptedException e) {
                throwException(new ThreadInterruptedLoggerException(e));
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
                throwException(new ThreadInterruptedLoggerException(e)).resetInterruptStatus();
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
            entry.setTimestamp(getClockTime());
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
        private final Builder<Profiler> profilerBuilder;

        private ProfiledConsumer(Builder<Profiler> profilerBuilder) {
            this.profilerBuilder = profilerBuilder;
        }

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
            boolean failure = joinAny(pipeline.getFilters(), filter -> () -> filter.test(entry, ParallelLogger.this), Boolean::booleanValue);
            if (failure) return;

            ProducerView producer = pipeline.getProducer();
            Map<Formatter, String> formattedFragments = new HashMap<>();
            failure = joinAny(producer.getFormatters(),
                    formatter -> () -> new AbstractMap.SimpleImmutableEntry<>(
                            formatter, formatter.apply(entry, ParallelLogger.this)),
                    entry -> {
                        Optional<String> formattedFragment = entry.getValue();
                        if (formattedFragment.isEmpty()) return true;
                        formattedFragments.put(entry.getKey(), formattedFragment.get());
                        return false;
                    });
            if (failure) return;
            String formattedEntry = producer.produce(formattedFragments);

            joinAll(pipeline.getConsumers().stream()
                    .map(consumer -> (Runnable) () -> consumer.accept(entry, formattedEntry, ParallelLogger.this))
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
}
