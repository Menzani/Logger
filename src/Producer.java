package it.menzani.logger;

import it.menzani.logger.api.Formatter;

import java.util.*;

public final class Producer {
    private final List<Object> elements = new ArrayList<>();
    private final Set<Formatter> formatters = new HashSet<>();

    public Set<Formatter> getFormatters() {
        return Collections.unmodifiableSet(formatters);
    }

    public Producer append(Formatter formatter) {
        elements.add(formatter);
        formatters.add(formatter);
        return this;
    }

    public Producer append(CharSequence charSequence) {
        elements.add(charSequence);
        return this;
    }

    public String produce(Map<Formatter, String> formattedElements) {
        StringBuilder builder = new StringBuilder();
        for (Object element : elements) {
            if (element instanceof Formatter) {
                builder.append(formattedElements.get(element));
            } else {
                builder.append((CharSequence) element);
            }
        }
        return builder.toString();
    }
}
