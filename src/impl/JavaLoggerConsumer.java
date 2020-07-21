/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.Consumer;
import eu.menzani.logger.api.Level;

import java.util.logging.Logger;

public final class JavaLoggerConsumer implements Consumer {
    private final Logger logger;

    public JavaLoggerConsumer(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void consume(LogEntry entry, String formattedEntry) {
        Level level = entry.getLevel();
        if (level instanceof StandardLevel) {
            consume(formattedEntry, (StandardLevel) level);
        } else if (level.isError()) {
            logger.severe(formattedEntry);
        } else {
            logger.info(formattedEntry);
        }
    }

    private void consume(String entry, StandardLevel level) {
        switch (level) {
            case TRACE:
                logger.finest(entry);
                break;
            case DEBUG:
                logger.finer(entry);
                break;
            case FINE:
                logger.fine(entry);
                break;
            case INFORMATION:
            case HEADER:
                logger.info(entry);
                break;
            case WARNING:
                logger.warning(entry);
                break;
            case FAILURE:
            case FATAL:
                logger.severe(entry);
                break;
        }
    }
}
