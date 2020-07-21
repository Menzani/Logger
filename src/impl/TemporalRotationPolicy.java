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

import eu.menzani.logger.api.RotationPolicy;
import eu.menzani.logger.Objects;
import eu.menzani.logger.StringFormat;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

public final class TemporalRotationPolicy implements RotationPolicy {
    private final StringFormat nameFormat;
    private final DateTimeFormatter timestampFormatter;
    private final int stepWidth;
    private final TemporalField stepWidthField;

    public TemporalRotationPolicy(String nameFormat, DateTimeFormatter timestampFormatter,
                                  int stepWidth, TemporalField stepWidthField) {
        if (stepWidth < 1) {
            throw new IllegalArgumentException("stepWidth must be positive.");
        }
        this.nameFormat = new StringFormat(Objects.objectNotNull(nameFormat, "nameFormat"));
        this.timestampFormatter = Objects.objectNotNull(timestampFormatter, "timestampFormatter");
        this.stepWidth = stepWidth;
        this.stepWidthField = Objects.objectNotNull(stepWidthField, "stepWidthField");
    }

    @Override
    public void initialize(Path root, Temporal timestamp) {
        // Do nothing
    }

    @Override
    public Path currentFile(Path root, Temporal timestamp) {
        TemporalAccessor roundedDown = timestamp.with(stepWidthField,
                timestamp.get(stepWidthField) / stepWidth * stepWidth);
        return root.resolve(nameFormat.clone().fill("timestamp", timestampFormatter.format(roundedDown)).toString());
    }
}
