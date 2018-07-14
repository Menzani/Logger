package it.menzani.logger;

import it.menzani.logger.api.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class Producer {
    private final List<Object> elements = new ArrayList<>();

    public Stream<Formatter> getFormatters() {
        return elements.stream()
                .filter(Formatter.class::isInstance)
                .map(Formatter.class::cast);
    }

    public Producer append(Formatter formatter) {
        elements.add(formatter);
        return this;
    }

    public Producer append(CharSequence charSequence) {
        elements.add(charSequence);
        return this;
    }

    public Optional<String> produce(Map<Formatter, Optional<String>> formattedElements) {
        if (!formattedElements.values().stream()
                .allMatch(Optional::isPresent)) return Optional.empty();
        StringBuilder builder = new StringBuilder();
        for (Object element : elements) {
            if (element instanceof Formatter) {
                Optional<String> formattedElement = formattedElements.get(element);
                assert formattedElement.isPresent();
                builder.append(formattedElement.get());
            } else {
                builder.append((CharSequence) element);
            }
        }
        return Optional.of(builder.toString());
    }
}
