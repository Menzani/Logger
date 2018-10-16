/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.AtomicLazy;
import it.menzani.logger.Lazy;
import it.menzani.logger.Objects;
import it.menzani.logger.api.Consumer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileConsumer implements Consumer, Lazy.Initializer<PrintWriter> {
    private final Path file;
    private final Lazy<PrintWriter> writer = new AtomicLazy<>(this, 10);

    public FileConsumer(Path file) {
        this.file = Objects.objectNotNull(file, "file");
    }

    @Override
    public void consume(LogEntry entry, String formattedEntry) throws Exception {
        PrintWriter writer = this.writer.get();
        writer.println(formattedEntry);
    }

    @Override
    public PrintWriter newInstance() throws IOException {
        return new PrintWriter(Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND), true);
    }
}
