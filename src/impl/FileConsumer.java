package it.menzani.logger.impl;

import it.menzani.logger.AtomicLazy;
import it.menzani.logger.Lazy;
import it.menzani.logger.api.Consumer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileConsumer implements Consumer, Lazy.Initializer<Writer> {
    private static final String lineSeparator = System.lineSeparator();

    private final Path file;
    private final Lazy<Writer> writer = new AtomicLazy<>(this, 10);

    public FileConsumer(Path file) {
        this.file = file;
    }

    @Override
    public void consume(LogEntry entry, String formattedEntry) throws Exception {
        Writer writer = this.writer.get();
        writer.write(formattedEntry);
        writer.write(lineSeparator);
        writer.flush();
    }

    @Override
    public Writer newInstance() throws IOException {
        return new OutputStreamWriter(Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }
}
