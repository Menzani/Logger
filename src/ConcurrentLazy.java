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

import java.util.concurrent.atomic.AtomicReference;

public final class ConcurrentLazy<V> implements Lazy<V> {
    private final Initializer<V> initializer;
    private final AtomicReference<V> value = new AtomicReference<>();

    public ConcurrentLazy(Initializer<V> initializer) {
        this.initializer = initializer;
    }

    @Override
    public V get() throws Exception {
        V value = this.value.getAcquire();
        if (value == null) {
            synchronized (this) {
                value = this.value.getAcquire();
                if (value == null) {
                    value = initializer.newInstance();
                    this.value.setRelease(value);
                }
            }
        }
        return value;
    }
}
