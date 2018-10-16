/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.api;

import it.menzani.logger.Objects;

import java.io.PrintStream;
import java.io.PrintWriter;

public abstract class LoggerException extends RuntimeException {
    protected LoggerException(String message, Exception exception) {
        super(Objects.objectNotNull(message, "message"), Objects.objectNotNull(exception, "exception"));
    }

    @Override
    public void printStackTrace(PrintStream s) {
        getCause().printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        getCause().printStackTrace(s);
    }
}
