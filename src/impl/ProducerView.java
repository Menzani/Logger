package it.menzani.logger.impl;

import it.menzani.logger.api.Formatter;

import java.util.*;

public final class ProducerView {
    private final List<Object> fragments;
    private final Set<Formatter> formatters;

    ProducerView(List<Object> fragments, Set<Formatter> formatters) {
        this.fragments = new ArrayList<>(fragments);
        this.formatters = Collections.unmodifiableSet(new HashSet<>(formatters));
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
