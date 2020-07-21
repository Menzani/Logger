/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.api;

import eu.menzani.logger.impl.EvaluationException;
import eu.menzani.logger.impl.LogEntry;
import eu.menzani.logger.impl.PipelineLoggerException;

import java.util.Optional;
import java.util.function.BiFunction;

public interface Formatter extends BiFunction<LogEntry, AbstractLogger, Optional<String>> {
    String format(LogEntry entry) throws Exception;

    @Override
    default Optional<String> apply(LogEntry entry, AbstractLogger logger) {
        try {
            return Optional.ofNullable(format(entry));
        } catch (EvaluationException e) {
            Object message = "Could not evaluate lazy message at level: " + entry.getLevel().getMarker();
            logger.throwable(AbstractLogger.ReservedLevel.ERROR, e.getCause(), message);
        } catch (Exception e) {
            logger.throwException(new PipelineLoggerException(e, PipelineLoggerException.PipelineElement.FORMATTER, this));
        }
        return Optional.empty();
    }
}
