package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.StringFormat;
import it.menzani.logger.api.RotationPolicy;

import java.nio.file.Files;
import java.nio.file.Path;

public final class StartupRotationPolicy implements RotationPolicy {
    private final StringFormat nameFormat;
    private Path currentFile;

    public StartupRotationPolicy(String nameFormat) {
        this.nameFormat = new StringFormat(Objects.objectNotNull(nameFormat, "nameFormat"));
    }

    public StartupRotationPolicy(String nameFormat, TimestampFormatter timestampFormatter) {
        this(nameFormat);
        Objects.objectNotNull(timestampFormatter, "timestampFormatter");
        this.nameFormat.evaluate("time", () -> timestampFormatter.format(null));
    }

    @Override
    public void initialize(Path root) throws Exception {
        for (short id = 1; id < Short.MAX_VALUE; id++) {
            Path file = root.resolve(nameFormat.evaluateAndClone().fill("id", id).toString());
            if (Files.notExists(file)) {
                currentFile = file;
                return;
            }
        }
        throw new RotationException("all IDs have been used");
    }

    @Override
    public Path currentFile() {
        return currentFile;
    }
}
