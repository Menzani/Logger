/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.Formatter;

import java.util.*;

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
