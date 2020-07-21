/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.menzani.logger;

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
