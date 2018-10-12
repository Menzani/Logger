package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.Formatter;
import it.menzani.logger.api.LoggerException;

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
