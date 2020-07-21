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

import eu.menzani.logger.Cloneable;
import eu.menzani.logger.impl.Pipeline;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class PipelineLogger extends ToggleableNamedLogger {
    private final Set<Pipeline> pipelines = new CopyOnWriteArraySet<>();

    protected PipelineLogger() {
        super();
    }

    protected PipelineLogger(String name) {
        super(name);
    }

    protected Set<Pipeline> getPipelines() {
        return Collections.unmodifiableSet(pipelines);
    }

    public Pipeline getPipeline(String name) {
        return pipelines.stream()
                .filter(pipeline -> pipeline.getName().equals(Optional.of(name))) // Implicit null check of `name`
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No pipeline found with name: " + name));
    }

    public PipelineLogger setPipelines(Pipeline... pipelines) {
        this.pipelines.clear();
        Collections.addAll(this.pipelines, pipelines);
        return this;
    }

    public PipelineLogger addPipeline(Pipeline pipeline) {
        pipelines.add(pipeline);
        return this;
    }

    public PipelineLogger profiled() {
        return profiled(profilerBuilder()
                .withMessageFormat(getName().orElse(getClass().getSimpleName()) + ": {ELAPSED}"));
    }

    public PipelineLogger profiled(ProfiledLogger.ProfilerBuilder profilerBuilder) {
        return new ProfiledLogger(profilerBuilder.withLogger(this));
    }

    @Override
    public PipelineLogger clone() {
        PipelineLogger clone = newInstance();
        pipelines.stream()
                .map(Cloneable::clone)
                .forEach(clone::addPipeline);
        return clone;
    }

    protected abstract PipelineLogger newInstance();

    public static ProfiledLogger.ProfilerBuilder profilerBuilder() {
        return new ProfiledLogger.ProfilerBuilder();
    }
}
