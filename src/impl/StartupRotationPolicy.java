package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.api.Formatter;
import it.menzani.logger.api.RotationPolicy;

import java.nio.file.Files;
import java.nio.file.Path;

public final class StartupRotationPolicy implements RotationPolicy {
    private final Object prefix;
    private final char prefixSeparator;
    private Path currentFile;

    public StartupRotationPolicy(String prefix, char prefixSeparator) {
        this((Object) prefix, prefixSeparator);
    }

    public StartupRotationPolicy(TimestampFormatter prefix, char prefixSeparator) {
        this((Object) prefix, prefixSeparator);
    }

    private StartupRotationPolicy(Object prefix, char prefixSeparator) {
        this.prefix = Objects.objectNotNull(prefix, "prefix");
        this.prefixSeparator = prefixSeparator;
    }

    @Override
    public void initialize(Path root) throws Exception {
        currentFile = nextAvailableFile(root, getPrefix());
    }

    @Override
    public Path currentFile() {
        return currentFile;
    }

    private String getPrefix() throws Exception {
        if (prefix instanceof String) {
            return (String) prefix + prefixSeparator;
        }
        if (prefix instanceof TimestampFormatter) {
            return ((Formatter) prefix).format(null) + prefixSeparator;
        }
        throw new AssertionError();
    }

    private Path nextAvailableFile(Path root, String prefix) {
        for (short suffix = 1; suffix < Short.MAX_VALUE; suffix++) {
            Path file = root.resolve(prefix + suffix);
            if (Files.notExists(file)) {
                return file;
            }
        }
        throw new RotationException("all suffixes have been used");
    }
}
