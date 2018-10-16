/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.api.ExceptionHandler;
import it.menzani.logger.api.LoggerException;

public final class ThrowingExceptionHandler implements ExceptionHandler {
    @Override
    public void handle(LoggerException exception) {
        throw exception;
    }
}
