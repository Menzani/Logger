/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.menzani.logger;

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
