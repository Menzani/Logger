package it.menzani.logger.lazy;

public interface Lazy<V> {
    V get() throws Exception;

    @FunctionalInterface
    interface Initializer<T> {
        T newInstance() throws Exception;
    }
}
