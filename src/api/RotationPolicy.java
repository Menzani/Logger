package it.menzani.logger.api;

import java.nio.file.Path;

public interface RotationPolicy {
    void initialize(Path root) throws Exception;

    Path currentFile() throws Exception;
}
