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

import eu.menzani.logger.Objects;
import eu.menzani.logger.SynchronizablePrintWriter;
import eu.menzani.logger.api.ExceptionHandler;
import eu.menzani.logger.api.LoggerException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileExceptionHandler implements ExceptionHandler {
    private final SynchronizablePrintWriter writer;

    public FileExceptionHandler(Path file) throws IOException {
        writer = new SynchronizablePrintWriter(Files.newOutputStream(
                Objects.objectNotNull(file, "file"), StandardOpenOption.CREATE, StandardOpenOption.APPEND), true);
    }

    @Override
    public void handle(LoggerException exception) {
        synchronized (writer.monitor()) {
            writer.println(exception.getMessage());
            exception.printStackTrace(writer);
        }
    }
}
