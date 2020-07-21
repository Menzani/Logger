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

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Collectors;

public abstract class AbstractBuilder<T> implements Builder<T> {
    private boolean locked;

    @Override
    public T build() {
        if (!locked) checkValid();
        return doBuild();
    }

    protected abstract T doBuild();

    @Override
    public Builder<T> lock() {
        if (!locked) {
            checkValid();
            locked = true;
        }
        return this;
    }

    private void checkValid() {
        Queue<String> missingProperties = new ArrayDeque<>();
        validate(missingProperties);
        if (!missingProperties.isEmpty()) throw new IllegalStateException(missingProperties.stream()
                .collect(Collectors.joining(", ", "These properties must be set: ", ".")));
    }

    protected abstract void validate(Queue<String> missingProperties);

    @Override
    public boolean isLocked() {
        return locked;
    }

    protected void checkLocked() {
        if (locked) throw new IllegalStateException("Builder is locked.");
    }
}
