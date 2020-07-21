/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

public final class RotationException extends RuntimeException {
    public RotationException(String cause) {
        super("Could not switch file: " + cause + '.');
    }

    public RotationException(Exception cause) {
        super("Could not switch file.", cause);
    }
}
