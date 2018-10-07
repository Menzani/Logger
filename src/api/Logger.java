package it.menzani.logger.api;

public interface Logger extends Named, Toggleable, Cloneable<Logger> {
    void trace(LazyMessage lazyMessage);

    void debug(LazyMessage lazyMessage);

    void fine(LazyMessage lazyMessage);

    void info(LazyMessage lazyMessage);

    void header(LazyMessage lazyMessage);

    void warn(LazyMessage lazyMessage);

    void fail(LazyMessage lazyMessage);

    void throwable(Throwable t, LazyMessage lazyMessage);

    void throwable(Level level, Throwable t, LazyMessage lazyMessage);

    void fatal(LazyMessage lazyMessage);

    void log(Level level, LazyMessage lazyMessage);


    void trace(String parameterizedMessage, Object... arguments);

    void debug(String parameterizedMessage, Object... arguments);

    void fine(String parameterizedMessage, Object... arguments);

    void info(String parameterizedMessage, Object... arguments);

    void header(String parameterizedMessage, Object... arguments);

    void warn(String parameterizedMessage, Object... arguments);

    void fail(String parameterizedMessage, Object... arguments);

    void throwable(Throwable throwable, String parameterizedMessage, Object... arguments);

    void throwable(Level level, Throwable throwable, String parameterizedMessage, Object... arguments);

    void fatal(String parameterizedMessage, Object... arguments);

    void log(Level level, String parameterizedMessage, Object... arguments);


    void trace(Object message);

    void debug(Object message);

    void fine(Object message);

    void info(Object message);

    void header(Object message);

    void warn(Object message);

    void fail(Object message);

    void throwable(Throwable t, Object message);

    void throwable(Level level, Throwable t, Object message);

    void fatal(Object message);

    void log(Level level, Object message);
}
