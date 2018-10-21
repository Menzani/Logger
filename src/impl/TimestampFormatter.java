/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.api.Formatter;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public final class TimestampFormatter implements Formatter {
    private final DateTimeFormatter formatter;

    public TimestampFormatter() {
        this(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public TimestampFormatter(DateTimeFormatter formatter) {
        this.formatter = Objects.objectNotNull(formatter, "formatter");
    }

    @Override
    public String format(LogEntry entry) {
        return formatter.format(entry.getTimestamp());
    }
}
