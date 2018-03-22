package it.menzani.logger.api;

import it.menzani.logger.Level;

public interface Consumer {
    void consume(String entry, Level level) throws Exception;
}
