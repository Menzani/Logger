/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.api;

import it.menzani.logger.impl.RotationException;

import java.nio.file.Path;
import java.time.temporal.Temporal;

public interface RotationPolicy {
    void initialize(Path root, Temporal timestamp) throws Exception;

    Path currentFile(Path root, Temporal timestamp) throws Exception;

    default void doInitialize(Path root, Temporal timestamp) {
        try {
            initialize(root, timestamp);
        } catch (RotationException e) {
            throw e;
        } catch (Exception e) {
            throw new RotationException(e);
        }
    }

    default Path getCurrentFile(Path root, Temporal timestamp) {
        try {
            return currentFile(root, timestamp);
        } catch (RotationException e) {
            throw e;
        } catch (Exception e) {
            throw new RotationException(e);
        }
    }
}