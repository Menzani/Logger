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
