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
