/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import eu.menzani.logger.api.Logger;
import eu.menzani.logger.impl.ConsoleConsumer;
import eu.menzani.logger.impl.Pipeline;
import eu.menzani.logger.impl.SynchronousLogger;

import java.util.UUID;

class ProfiledLoggerTest {
    public static void main(String[] args) {
        Logger logger = new SynchronousLogger()
                .addPipeline(new Pipeline()
                        .addConsumer(new ConsoleConsumer()))
                .profiled();
        logger.info(UUID.randomUUID());
        logger.info(UUID.randomUUID());
    }
}
