package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.SynchronizablePrintWriter;
import it.menzani.logger.api.ExceptionHandler;
import it.menzani.logger.api.LoggerException;

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
