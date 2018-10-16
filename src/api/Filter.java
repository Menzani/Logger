/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.api;

import it.menzani.logger.impl.LogEntry;
import it.menzani.logger.impl.PipelineLoggerException;

import java.util.function.BiPredicate;

public interface Filter extends BiPredicate<LogEntry, AbstractLogger> {
    boolean reject(LogEntry entry) throws Exception;

    @Override
    default boolean test(LogEntry entry, AbstractLogger logger) {
        try {
            return reject(entry);
        } catch (Exception e) {
            logger.throwException(new PipelineLoggerException(e, PipelineLoggerException.PipelineElement.FILTER, this));
            return true;
        }
    }
}
