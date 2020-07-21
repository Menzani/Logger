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
