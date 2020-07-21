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

import eu.menzani.logger.api.Filter;
import eu.menzani.logger.api.Level;

final class LevelFilter implements Filter {
    private final Level level;

    LevelFilter(Level level) {
        this.level = level;
    }

    Level getLevel() {
        return level;
    }

    @Override
    public boolean reject(LogEntry entry) {
        return entry.getLevel().compareTo(level) == Level.Verbosity.GREATER;
    }
}
