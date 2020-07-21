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
