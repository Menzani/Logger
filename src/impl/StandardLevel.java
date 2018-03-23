package it.menzani.logger.impl;

import it.menzani.logger.api.Level;

public enum StandardLevel implements Level {
    FINE(4, "FINE"),
    INFORMATION(3, "INFO"),
    HEADER(2, "HEADER"),
    WARNING(1, "WARNING"),
    FAILURE(0, "FAILURE");

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
