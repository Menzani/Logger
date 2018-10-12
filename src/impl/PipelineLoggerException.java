package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Filter;
import it.menzani.logger.api.Formatter;
import it.menzani.logger.api.LoggerException;

public final class PipelineLoggerException extends LoggerException {
    private final Class<?> apiClass;
    private final Object implObject;

    public PipelineLoggerException(Exception exception, Class<?> apiClass, Object implObject) {
        super("Could not pass log entry to " + Objects.objectNotNull(apiClass, "apiClass").getSimpleName() + ": " +
                Objects.objectNotNull(implObject, "implObject").getClass().getName(), exception);
        this.apiClass = apiClass;
        this.implObject = implObject;
        if (!(implObject instanceof Filter || implObject instanceof Formatter || implObject instanceof Consumer)) {
            throw new IllegalArgumentException("implObject must be a Filter, Formatter, or Consumer.");
        }
        if (!(isFilterError() || isFormatterError() || isConsumerError())) {
            throw new IllegalArgumentException("apiClass must refer to a Filter, Formatter, or Consumer.");
        }
    }

    public Class<?> getApiClass() {
        return apiClass;
    }

    public Object getImplObject() {
        return implObject;
    }

    public boolean isFilterError() {
        return apiClass == Filter.class;
    }

    public boolean isFormatterError() {
        return apiClass == Formatter.class;
    }

    public boolean isConsumerError() {
        return apiClass == Consumer.class;
    }
}
