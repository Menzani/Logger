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

package eu.menzani.logger.impl;

import eu.menzani.logger.Nullable;
import eu.menzani.logger.Objects;
import eu.menzani.logger.StringFormat;
import eu.menzani.logger.api.RotationPolicy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public final class StartupRotationPolicy implements RotationPolicy {
    private final StringFormat nameFormat;
    private final DateTimeFormatter timestampFormatter;
    private Path currentFile;

    public StartupRotationPolicy(String nameFormat) {
        this(nameFormat, null);
    }

    public StartupRotationPolicy(String nameFormat, @Nullable DateTimeFormatter timestampFormatter) {
        this.nameFormat = new StringFormat(Objects.objectNotNull(nameFormat, "nameFormat"));
        this.timestampFormatter = timestampFormatter;
    }

    @Override
    public void initialize(Path root, Temporal timestamp) {
        if (timestampFormatter != null) {
            nameFormat.fill("timestamp", timestampFormatter.format(timestamp));
        }
        for (short id = 1; id < Short.MAX_VALUE; id++) {
            Path file = root.resolve(nameFormat.clone().fill("id", id).toString());
            if (Files.notExists(file)) {
                currentFile = file;
                return;
            }
        }
        throw new RotationException("all IDs have been used");
    }

    @Override
    public Path currentFile(Path root, Temporal timestamp) {
        return currentFile;
    }
}
