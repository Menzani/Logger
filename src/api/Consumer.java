package it.menzani.logger.api;

public interface Consumer {
    void consume(String entry, Level level) throws Exception;
}
