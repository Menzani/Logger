/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
