package it.menzani.logger.api;

import java.time.temporal.Temporal;

@FunctionalInterface
public interface Clock {
    Temporal now() throws Exception;
}
