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

package eu.menzani.logger.impl;

import eu.menzani.logger.api.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProducerView {
    private final List<Object> fragments;
    private final Set<Formatter> formatters;

    ProducerView(List<Object> fragments, Set<Formatter> formatters) {
        this.fragments = new ArrayList<>(fragments);
        this.formatters = Set.copyOf(formatters);
    }

    public Set<Formatter> getFormatters() {
        return formatters;
    }

    public String produce(Map<Formatter, String> formattedFragments) {
        StringBuilder builder = new StringBuilder();
        for (Object fragment : fragments) {
            if (fragment instanceof Formatter) {
                builder.append(formattedFragments.get(fragment));
            } else if (fragment instanceof CharSequence) {
                builder.append((CharSequence) fragment);
            } else if (fragment instanceof Character) {
                builder.append((char) fragment);
            } else {
                throw new AssertionError();
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return formatters.size() + "+" + (fragments.size() - formatters.size());
    }
}
