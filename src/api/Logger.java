package it.menzani.logger.api;

public interface Logger {
    void fine(LazyMessage lazyMessage);

    void info(LazyMessage lazyMessage);

    void header(LazyMessage lazyMessage);

    void warn(LazyMessage lazyMessage);

    void fail(LazyMessage lazyMessage);

    void throwable(Throwable t, LazyMessage lazyMessage);

    void throwable(Level level, Throwable t, LazyMessage lazyMessage);

    void log(Level level, LazyMessage lazyMessage);

    void fine(Object message);

    void info(Object message);

    void header(Object message);

    void warn(Object message);

    void fail(Object message);

    void throwable(Throwable t, Object message);

    void throwable(Level level, Throwable t, Object message);

    void log(Level level, Object message);
}
