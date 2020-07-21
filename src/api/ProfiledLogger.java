/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.api;

import eu.menzani.logger.Profiler;
import eu.menzani.logger.impl.LogEntry;
import eu.menzani.logger.impl.Pipeline;

import java.util.Queue;

public final class ProfiledLogger extends PipelineLogger {
    private final ProfilerBuilder builder;

    ProfiledLogger(ProfilerBuilder builder) {
        this.builder = builder.lock();
    }

    @Override
    public PipelineLogger setClock(Clock clock) {
        builder.logger.setClock(clock);
        return this;
    }

    @Override
    public PipelineLogger setExceptionHandler(ExceptionHandler exceptionHandler) {
        builder.logger.setExceptionHandler(exceptionHandler);
        return this;
    }

    @Override
    public Pipeline getPipeline(String name) {
        return builder.logger.getPipeline(name);
    }

    @Override
    public PipelineLogger setPipelines(Pipeline... pipelines) {
        builder.logger.setPipelines(pipelines);
        return this;
    }

    @Override
    public PipelineLogger addPipeline(Pipeline pipeline) {
        builder.logger.addPipeline(pipeline);
        return this;
    }

    @Override
    public PipelineLogger profiled() {
        throw newProfilingActiveException();
    }

    @Override
    public PipelineLogger profiled(ProfilerBuilder profilerBuilder) {
        throw newProfilingActiveException();
    }

    private static RuntimeException newProfilingActiveException() {
        return new UnsupportedOperationException("Profiling is active.");
    }

    @Override
    public PipelineLogger clone() {
        return builder.logger.clone();
    }

    @Override
    protected PipelineLogger newInstance() {
        throw new AssertionError();
    }

    @Override
    protected void doLog(LogEntry entry) {
        try (Profiler ignored = builder.build()) {
            builder.logger.doLog(entry);
        }
    }

    public static final class ProfilerBuilder extends Profiler.Builder {
        private PipelineLogger logger;

        ProfilerBuilder() {
        }

        @Override
        public Profiler.Builder withLogger(Logger logger) {
            throw new UnsupportedOperationException("Use #withLogger(PipelineLogger) instead.");
        }

        public ProfilerBuilder withLogger(PipelineLogger logger) {
            checkLocked();
            this.logger = logger;
            return this;
        }

        @Override
        public ProfilerBuilder withLevel(Level level) {
            super.withLevel(level);
            return this;
        }

        @Override
        public ProfilerBuilder withMessageFormat(String messageFormat) {
            super.withMessageFormat(messageFormat);
            return this;
        }

        @Override
        public ProfilerBuilder lock() {
            super.lock();
            return this;
        }

        @Override
        protected void validate(Queue<String> missingProperties) {
            super.validate(missingProperties);
            assert logger != null;
        }
    }
}
