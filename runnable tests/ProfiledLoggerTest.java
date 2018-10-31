/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import it.menzani.logger.api.Logger;
import it.menzani.logger.impl.ConsoleConsumer;
import it.menzani.logger.impl.Pipeline;
import it.menzani.logger.impl.SynchronousLogger;

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
