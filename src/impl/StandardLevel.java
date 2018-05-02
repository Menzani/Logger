package it.menzani.logger.impl;

import it.menzani.logger.api.Level;

public enum StandardLevel implements Level {
    TRACE(7_000_000, "TRACE", false),
    DEBUG(6_000_000, "DEBUG", false),
    FINE(5_000_000, "FINE", false),
    INFORMATION(4_000_000, "INFO", false),
    HEADER(3_000_000, "HEADER", false),
    WARNING(2_000_000, "WARN", false),
    FAILURE(1_000_000, "FAIL", true),
    FATAL(0, "FATAL", true);

    private final int verbosity;
    private final String marker;
    private final boolean error;

    StandardLevel(int verbosity, String marker, boolean error) {
        this.verbosity = verbosity;
        this.marker = marker;
        this.error = error;
    }

    @Override
    public int getVerbosity() {
        return verbosity;
    }

    @Override
    public String getMarker() {
        return marker;
    }
	
    @Override
    public boolean isError() {
        return error;
    }
}