/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
