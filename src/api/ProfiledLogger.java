package it.menzani.logger.api;

import it.menzani.logger.Profiler;
import it.menzani.logger.impl.LogEntry;
import it.menzani.logger.impl.Pipeline;

public final class ProfiledLogger extends PipelineLogger {
    private final ProfilerBuilder builder;

    ProfiledLogger(ProfilerBuilder builder) {
        this.builder = builder.lock();
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
        return builder.logger.newInstance();
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
        public ProfilerBuilder withFormat(String format) {
            super.withFormat(format);
            return this;
        }

        @Override
        public void validate() {
            super.validate();
            assert logger != null;
        }

        @Override
        public ProfilerBuilder lock() {
            super.lock();
            return this;
        }
    }
}
