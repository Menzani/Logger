package it.menzani.logger.lazy;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// https://stackoverflow.com/a/20147562/3453226
public final class AtomicLazy<V> implements Lazy<V> {
    private final AtomicReference<V> cache = new AtomicReference<>();
    private final AtomicBoolean isComputing = new AtomicBoolean();
    private final Initializer<V> initializer;
    private final long maxCacheCheckInterval;

    public AtomicLazy(Initializer<V> initializer, long maxCacheCheckInterval) {
        this.initializer = initializer;
        this.maxCacheCheckInterval = maxCacheCheckInterval;
    }

    @Override
    public V get() throws Exception {
        V result = cache.get();
        if (result == null) {
            if (isComputing.compareAndSet(false, true)) {
                result = initializer.newInstance();
                cache.set(result);
                notifyAll();
            } else {
                while ((result = cache.get()) == null) {
                    wait(maxCacheCheckInterval); // Timeout prevents deadlock
                }
            }
        }
        return result;
    }
}
