package it.menzani.logger;

public interface Builder<T> {
    void validate();

    T build();

    Builder<T> lock();

    static RuntimeException newLockedException() {
        return new IllegalStateException("Builder is locked.");
    }

    static RuntimeException newValidationException(String propertyName) {
        return new IllegalStateException(propertyName + " property must be set.");
    }
}
