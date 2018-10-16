/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.api.Consumer;

public final class ConsoleConsumer implements Consumer {
    @Override
    public void consume(LogEntry entry, String formattedEntry) {
        if (entry.getLevel().isError()) {
            System.err.println(formattedEntry);
        } else {
            System.out.println(formattedEntry);
        }
    }
}
