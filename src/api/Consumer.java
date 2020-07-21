/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.api;

import eu.menzani.logger.impl.LogEntry;
import eu.menzani.logger.impl.PipelineLoggerException;

public interface Consumer {
    void consume(LogEntry entry, String formattedEntry) throws Exception;

    default void accept(LogEntry entry, String formattedEntry, AbstractLogger logger) {
        try {
            consume(entry, formattedEntry);
        } catch (Exception e) {
            logger.throwException(new PipelineLoggerException(e, PipelineLoggerException.PipelineElement.CONSUMER, this));
        }
    }
}
