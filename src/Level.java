package it.menzani.logger;

public enum Level {
    FINE(4, "FINE"),
    INFORMATION(3, "INFO"),
    HEADER(2, "HEADER"),
    WARNING(1, "WARNING"),
    FAILURE(0, "FAILURE");

    private final int verbosity;
    private final String marker;

    Level(int verbosity, String marker) {
        this.verbosity = verbosity;
        this.marker = marker;
    }

    public int getVerbosity() {
        return verbosity;
    }

    public String getMarker() {
        return marker;
    }
}
