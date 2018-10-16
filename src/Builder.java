/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger;

public interface Builder<T> {
    void validate();

    T build();

    Builder<T> lock();

    static RuntimeException newLockedException() {
        return new IllegalStateException("Builder is locked.");
    }

    static RuntimeException newValidationException(String propertyName) {
        return new IllegalStateException(propertyName + " property must be set.");
    }
}
