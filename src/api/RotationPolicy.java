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

package eu.menzani.logger.api;

import eu.menzani.logger.impl.RotationException;

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
