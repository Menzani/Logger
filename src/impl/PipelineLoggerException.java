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

import eu.menzani.logger.Objects;
import eu.menzani.logger.api.Consumer;
import eu.menzani.logger.api.Filter;
import eu.menzani.logger.api.Formatter;
import eu.menzani.logger.api.LoggerException;

public final class PipelineLoggerException extends LoggerException {
    private final PipelineElement pipelineElement;
    private final Object implObject;

    public PipelineLoggerException(Exception exception, PipelineElement pipelineElement, Object implObject) {
        super("Could not pass log entry to " + Objects.objectNotNull(pipelineElement, "pipelineElement") + ": " +
                Objects.objectNotNull(implObject, "implObject").getClass().getName(), exception);
        switch (pipelineElement) {
            case FILTER:
                if (implObject instanceof Filter) break;
            case FORMATTER:
                if (implObject instanceof Formatter) break;
            case CONSUMER:
                if (implObject instanceof Consumer) break;
            default:
                throw new IllegalArgumentException("implObject must be a " + pipelineElement + '.');
        }
        this.pipelineElement = pipelineElement;
        this.implObject = implObject;
    }

    public PipelineElement getPipelineElement() {
        return pipelineElement;
    }

    public Object getImplObject() {
        return implObject;
    }

    public enum PipelineElement {
        FILTER("Filter"),
        FORMATTER("Formatter"),
        CONSUMER("Consumer");

        private final String apiClass;

        PipelineElement(String apiClass) {
            this.apiClass = apiClass;
        }

        @Override
        public String toString() {
            return apiClass;
        }
    }
}
