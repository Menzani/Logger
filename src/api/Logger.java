package it.menzani.logger.api;

import it.menzani.logger.Level;

public interface Logger {
    void fine(LazyMessage lazyMessage);

    void info(LazyMessage lazyMessage);

    void header(LazyMessage lazyMessage);

    void warn(LazyMessage lazyMessage);

    void fail(LazyMessage lazyMessage);

    void throwable(Throwable t, LazyMessage lazyMessage);

    void log(Level level, LazyMessage lazyMessage);
}
