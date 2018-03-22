package it.menzani.logger.api;

@FunctionalInterface
public interface LazyMessage {
    Object evaluate() throws Exception;
}
