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
