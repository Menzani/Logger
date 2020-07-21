/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.api;

import eu.menzani.logger.impl.LogEntry;
import eu.menzani.logger.impl.PipelineLoggerException;

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
