package it.menzani.logger.api;

public interface Level {
    int getVerbosity();

    String getMarker();

    boolean isError();
}
