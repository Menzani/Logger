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

import eu.menzani.logger.api.Level;

public enum StandardLevel implements Level {
    TRACE(7_000_000, "TRACE", false),
    DEBUG(6_000_000, "DEBUG", false),
    FINE(5_000_000, "FINE", false),
    INFORMATION(4_000_000, "INFO", false),
    HEADER(3_000_000, "HEADER", false),
    WARNING(2_000_000, "WARN", false),
    FAILURE(1_000_000, "FAIL", true),
    FATAL(0, "FATAL", true);

    private final int verbosity;
    private final String marker;
    private final boolean error;

    StandardLevel(int verbosity, String marker, boolean error) {
        this.verbosity = verbosity;
        this.marker = marker;
        this.error = error;
    }

    @Override
    public int getVerbosity() {
        return verbosity;
    }

    @Override
    public String getMarker() {
        return marker;
    }

    @Override
    public boolean isError() {
        return error;
    }
}
