package it.menzani.logger.api;

enum ReservedLevel implements Level {
    LOGGER(-1, "LOGGER");

    private final int verbosity;
    private final String marker;

    ReservedLevel(int verbosity, String marker) {
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
