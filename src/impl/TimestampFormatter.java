/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.Objects;
import it.menzani.logger.api.Clock;
import it.menzani.logger.api.Formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;

public final class TimestampFormatter implements Formatter {
    private final Clock clock;
    private final DateTimeFormatter formatter;
    private volatile boolean timestampSource = true;

    public TimestampFormatter() {
        this(LocalDateTime::now, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public TimestampFormatter(Clock clock, DateTimeFormatter formatter) {
        this.clock = Objects.objectNotNull(clock, "clock");
        this.formatter = Objects.objectNotNull(formatter, "formatter");
    }

    public TimestampFormatter setTimestampSource(boolean timestampSource) {
        this.timestampSource = timestampSource;
        return this;
    }

    @Override
    public String format(LogEntry entry) throws Exception {
        Temporal timestamp = Objects.objectNotNull(clock.now(), "clock#now()");
        if (timestampSource) entry.setTimestamp(timestamp);
        return formatter.format(timestamp);
    }
}
