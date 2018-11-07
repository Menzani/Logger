/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.RotationPolicy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.temporal.Temporal;
import java.util.Optional;

public final class RotatingFileConsumer implements Consumer {
    private final Path root;
    private RotationPolicy policy;
    private volatile LogFile currentFile;

    public RotatingFileConsumer(Path root) {
        if (Files.isRegularFile(Objects.objectNotNull(root, "root"))) {
            throw new IllegalArgumentException("root must not be an existing file.");
        }
        this.root = root;
    }

    public synchronized RotatingFileConsumer setPolicy(RotationPolicy policy) {
        this.policy = Objects.objectNotNull(policy, "policy");
        currentFile = null;
        return this;
    }

    @Override
    public void consume(LogEntry entry, String formattedEntry) throws Exception {
        synchronized (this) {
            shouldRotate(entry.getTimestamp()).ifPresent(newerFile -> currentFile = newerFile);
        }
        currentFile.writer.println(formattedEntry);
    }

    private Optional<LogFile> shouldRotate(Temporal timestamp) throws IOException {
        if (currentFile == null) {
            Files.createDirectories(root);
            policy.doInitialize(root, timestamp);
            return Optional.of(new LogFile(policy.getCurrentFile(root, timestamp)));
        }
        Path currentFilePath = policy.getCurrentFile(root, timestamp);
        if (currentFile.path.equals(currentFilePath)) {
            return Optional.empty();
        }
        currentFile.writer.close();
        return Optional.of(new LogFile(currentFilePath));
    }

    private static final class LogFile {
        private final Path path;
        private final PrintWriter writer;

        private LogFile(Path path) throws IOException {
            this.path = Objects.objectNotNull(path, "policy#currentFile()");
            OutputStream stream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            writer = new PrintWriter(stream, true);
        }
    }
}