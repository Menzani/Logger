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

import eu.menzani.logger.api.LazyMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class StringFormat implements Cloneable<StringFormat> {
    private String format;
    private char start = '{', end = '}';
    private final Map<String, Object> valueSuppliers;

    public StringFormat(String format) {
        this(Objects.objectNotNull(format, "format"), new HashMap<>());
    }

    private StringFormat(String format, HashMap<String, Object> valueSuppliers) {
        this.format = format;
        this.valueSuppliers = valueSuppliers;
    }

    public StringFormat delimiters(char start, char end) {
        this.start = start;
        this.end = end;
        return this;
    }

    public StringFormat fill(String variable, Object value) {
        format = format.replace(start + Objects.objectNotNull(variable, "variable").toUpperCase() + end,
                Objects.objectNotNull(value, "value").toString());
        return this;
    }

    public StringFormat set(String variable, Supplier<?> valueSupplier) {
        put(variable, valueSupplier);
        return this;
    }

    public StringFormat evaluate(String variable, LazyMessage valueSupplier) {
        put(variable, valueSupplier);
        return this;
    }

    private void put(String variable, Object valueSupplier) {
        valueSuppliers.put(Objects.objectNotNull(variable, "variable"), Objects.objectNotNull(valueSupplier, "valueSupplier"));
    }

    @Override
    public String toString() {
        for (Map.Entry<String, Object> entry : valueSuppliers.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof LazyMessage) {
                throw new IllegalStateException("Value supplier of type LazyMessage found. Use #evaluateToString() instead.");
            }
            if (value instanceof Supplier<?>) {
                fill(entry.getKey(), ((Supplier<?>) value).get());
            } else {
                throw new AssertionError();
            }
        }
        valueSuppliers.clear();
        return format;
    }

    public String evaluateToString() throws Exception {
        for (Map.Entry<String, Object> entry : valueSuppliers.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof LazyMessage) {
                fill(key, ((LazyMessage) value).evaluate());
            } else if (value instanceof Supplier<?>) {
                fill(key, ((Supplier<?>) value).get());
            } else {
                throw new AssertionError();
            }
        }
        valueSuppliers.clear();
        return format;
    }

    @Override
    public StringFormat clone() {
        return new StringFormat(format, new HashMap<>(valueSuppliers)).delimiters(start, end);
    }
}
