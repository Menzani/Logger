package it.menzani.logger.impl;

import it.menzani.logger.Level;
import it.menzani.logger.api.Consumer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileConsumer implements Consumer {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Path file;
    private Writer writer;

    public FileConsumer(Path file) {
        this.file = file;
    }

    @Override
    public void consume(String entry, Level level) throws IOException {
        if (writer == null) {
            writer = new OutputStreamWriter(Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
        }
        writer.write(entry);
        writer.write(LINE_SEPARATOR);
        writer.flush();
    }
}
