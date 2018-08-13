package it.menzani.logger.impl;

import it.menzani.logger.api.Formatter;

import java.util.*;

public final class Producer {
    private final List<Object> fragments = new ArrayList<>();
    private final Set<Formatter> formatters = new HashSet<>();

    public Set<Formatter> getFormatters() {
        return Collections.unmodifiableSet(formatters);
    }

    public Producer append(Formatter formatter) {
        fragments.add(formatter);
        formatters.add(formatter);
        return this;
    }

    public Producer append(CharSequence charSequence) {
        fragments.add(charSequence);
        return this;
    }

    public Producer append(Character character) {
        fragments.add(character);
        return this;
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
