/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.Formatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Producer {
    private final List<Object> fragments = new ArrayList<>();
    private final Set<Formatter> formatters = new HashSet<>();

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

    ProducerView asView() {
        return new ProducerView(fragments, formatters);
    }
}
