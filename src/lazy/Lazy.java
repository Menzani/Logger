package it.menzani.logger.lazy;

public interface Lazy<V> {
    V get() throws Exception;
}
