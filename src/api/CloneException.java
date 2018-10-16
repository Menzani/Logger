/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.api;

import it.menzani.logger.Objects;

public final class CloneException extends RuntimeException {
    public CloneException(Cloneable<?> cloneable) {
        super("Could not produce clone of " + Objects.objectNotNull(cloneable, "cloneable").getClass().getName());
    }
}
