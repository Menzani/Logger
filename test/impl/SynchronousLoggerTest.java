/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.impl;

import it.menzani.logger.api.AbstractLoggerTest;
import it.menzani.logger.api.PipelineLogger;

class SynchronousLoggerTest extends AbstractLoggerTest {
    @Override
    protected PipelineLogger newLogger(Pipeline pipeline) {
        return new SynchronousLogger()
                .addPipeline(pipeline);
    }
}