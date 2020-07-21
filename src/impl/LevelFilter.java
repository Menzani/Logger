/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
