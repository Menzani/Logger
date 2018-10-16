/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// https://stackoverflow.com/a/20147562/3453226
public final class AtomicLazy<V> implements Lazy<V> {
    private final AtomicReference<V> cache = new AtomicReference<>();
    private final AtomicBoolean isComputing = new AtomicBoolean();
    private final Initializer<V> initializer;
    private final Object cacheCheckMonitor = new Object();
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
                synchronized (cacheCheckMonitor) {
                    cacheCheckMonitor.notifyAll();
                }
            } else {
                synchronized (cacheCheckMonitor) {
                    while ((result = cache.get()) == null) {
                        cacheCheckMonitor.wait(maxCacheCheckInterval); // Timeout prevents deadlock
                    }
                }
            }
        }
        return result;
    }
}
