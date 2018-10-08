package it.menzani.logger;

import java.util.Collection;

/**
 * These utility methods can be used to perform runtime checks of types in method parameters which do not accept the {@code null} value.
 * <p/>
 * In cases where a value can be {@code null}, consider annotating the corresponding type as {@link Nullable}.
 */
public final class Objects {
    private Objects() {
    }

    public static <T> T objectNotNull(T object, String variableName) {
        if (object == null) {
            throw new NullPointerException(variableName + " must not be null.");
        }
        return object;
    }

    public static <T> T elementNotNull(T element, String containerVariableName) {
        if (element == null) {
            throw new NullPointerException(containerVariableName + " must not contain null elements.");
        }
        return element;
    }

    public static <T> T[] contentNotNull(T[] array, String variableName) {
        for (T element : array) {
            elementNotNull(element, variableName);
        }
        return array;
    }

    public static <T> Collection<T> contentNotNull(Collection<T> collection, String variableName) {
        for (T element : collection) {
            elementNotNull(element, variableName);
        }
        return collection;
    }

    public static <T> T[] deepNotNull(T[] array, String variableName) {
        objectNotNull(array, variableName);
        contentNotNull(array, variableName);
        return array;
    }

    public static <T> Collection<T> deepNotNull(Collection<T> collection, String variableName) {
        objectNotNull(collection, variableName);
        contentNotNull(collection, variableName);
        return collection;
    }
}
