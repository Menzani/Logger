package it.menzani.logger.lazy;

@FunctionalInterface
public interface Initializer<T> {
    T newInstance() throws Exception;
}
