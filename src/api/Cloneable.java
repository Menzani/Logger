package it.menzani.logger.api;

import it.menzani.logger.CloneException;

public interface Cloneable<T> {
    T clone() throws CloneException;
}
