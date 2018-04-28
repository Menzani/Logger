package it.menzani.logger.api;

enum ReservedLevel implements Level {
    LOGGER(-1, "LOGGER", false);

    private final int verbosity;
    private final String marker;
    private final boolean error;

    ReservedLevel(int verbosity, String marker, boolean error) {
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
