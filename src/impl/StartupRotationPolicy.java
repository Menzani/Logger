package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.StringFormat;
import it.menzani.logger.api.RotationPolicy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public final class StartupRotationPolicy implements RotationPolicy {
    private final StringFormat nameFormat;
    private final DateTimeFormatter timestampFormatter;
    private Path currentFile;

    public StartupRotationPolicy(String nameFormat) {
        this(nameFormat, null);
    }

    public StartupRotationPolicy(String nameFormat, DateTimeFormatter timestampFormatter) {
        this.nameFormat = new StringFormat(Objects.objectNotNull(nameFormat, "nameFormat"));
        this.timestampFormatter = timestampFormatter;
    }

    @Override
    public void initialize(Path root, Temporal timestamp) {
        nameFormat.fill("timestamp", timestampFormatter.format(timestamp));
        for (short id = 1; id < Short.MAX_VALUE; id++) {
            Path file = root.resolve(nameFormat.clone().fill("id", id).toString());
            if (Files.notExists(file)) {
                currentFile = file;
                return;
            }
        }
        throw new RotationException("all IDs have been used");
    }

    @Override
    public Path currentFile(Path root, Temporal timestamp) {
        return currentFile;
    }
}
