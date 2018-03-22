package it.menzani.logger;

public enum Level {
    FINE("FINE"),
    INFORMATION("INFO"),
    HEADER("HEADER"),
    WARNING("WARNING"),
    FAILURE("FAILURE");

    private final String marker;

    Level(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return marker;
    }
}
