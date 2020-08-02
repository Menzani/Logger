/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.ConcurrentLazy;
import eu.menzani.logger.Lazy;
import eu.menzani.logger.Objects;
import eu.menzani.logger.api.Consumer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileConsumer implements Consumer, Lazy.Initializer<PrintWriter> {
    private final Path file;
    private final Lazy<PrintWriter> writer = new ConcurrentLazy<>(this);

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
