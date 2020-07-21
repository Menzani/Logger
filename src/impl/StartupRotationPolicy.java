/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.RotationPolicy;
import eu.menzani.logger.Nullable;
import eu.menzani.logger.Objects;
import eu.menzani.logger.StringFormat;

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
