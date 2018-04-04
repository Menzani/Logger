package it.menzani.logger.impl;

import it.menzani.logger.api.Level;

public enum StandardLevel implements Level {
    TRACE(7_000_000, "TRACE"),
    DEBUG(6_000_000, "DEBUG"),
    FINE(5_000_000, "FINE"),
    INFORMATION(4_000_000, "INFO"),
    HEADER(3_000_000, "HEADER"),
    WARNING(2_000_000, "WARN"),
    FAILURE(1_000_000, "FAIL"),
    FATAL(0, "FATAL");

    private final int verbosity;
    private final String marker;

    StandardLevel(int verbosity, String marker) {
        this.verbosity = verbosity;
        this.marker = marker;
    }

    @Override
    public int getVerbosity() {
        return verbosity;
    }

    @Override
    public String getMarker() {
        return marker;
    }
}
