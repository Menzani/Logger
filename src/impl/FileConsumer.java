package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Consumer;
import it.menzani.logger.lazy.AtomicLazy;
import it.menzani.logger.lazy.Initializer;
import it.menzani.logger.lazy.Lazy;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileConsumer implements Consumer, Initializer<Writer> {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Path file;
    private final Lazy<Writer> writer = new AtomicLazy<>(this, 10);

    public FileConsumer(Path file) {
        this.file = file;
    }

    @Override
    public void consume(LogEntry entry, String formattedEntry) throws Exception {
        Writer writer = this.writer.get();
        writer.write(formattedEntry);
        writer.write(LINE_SEPARATOR);
        writer.flush();
    }

    @Override
    public Writer newInstance() throws IOException {
        return new OutputStreamWriter(Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }
}
