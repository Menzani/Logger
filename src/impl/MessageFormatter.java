/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger.impl;

import eu.menzani.logger.api.Formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageFormatter implements Formatter {
    private static final Pattern markers = Pattern.compile("%n|\\r?\\n");
    private static final String lineSeparator = System.lineSeparator();

    @Override
    public String format(LogEntry entry) throws EvaluationException {
        Matcher matcher = markers.matcher(entry.getMessage().toString());
        return matcher.replaceAll(lineSeparator);
    }
}
