package it.menzani.logger.api;

import it.menzani.logger.impl.RotationException;

import java.nio.file.Path;

public interface RotationPolicy {
    void initialize(Path root) throws Exception;

    Path currentFile() throws Exception;

    default void doInitialize(Path root) {
        try {
            initialize(root);
        } catch (RotationException e) {
            throw e;
        } catch (Exception e) {
            throw new RotationException(e);
        }
    }

    default Path getCurrentFile() {
        try {
            return currentFile();
        } catch (RotationException e) {
            throw e;
        } catch (Exception e) {
            throw new RotationException(e);
        }
    }
}
