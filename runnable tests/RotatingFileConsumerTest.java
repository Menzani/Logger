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
import eu.menzani.logger.api.RotationPolicy;
import eu.menzani.logger.impl.*;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class RotatingFileConsumerTest {
    public static void main(String[] args) {
        Logger logger = new SynchronousLogger()
                .addPipeline(new Pipeline()
                        .setProducer(new Producer()
                                .append(new TimestampFormatter())
                                .append(" -> ")
                                .append(new MessageFormatter()))
                        .addConsumer(new ConsoleConsumer())
                        .addConsumer(new RotatingFileConsumer(Runtime.folder).setPolicy(createRotationPolicy())));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> logger.info(UUID.randomUUID()), 0, 500, TimeUnit.MILLISECONDS);
    }

    private static RotationPolicy createRotationPolicy() {
        switch (Runtime.Setting.ROTATION_POLICY.get()) {
            case "startup":
                return newStartupRotationPolicy();
            case "temporal":
                return newTemporalRotationPolicy();
            default:
                throw Runtime.Setting.ROTATION_POLICY.newInvalidValueException();
        }
    }

    private static RotationPolicy newStartupRotationPolicy() {
        return new StartupRotationPolicy("Startup {TIMESTAMP}-{ID}.log",
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    private static RotationPolicy newTemporalRotationPolicy() {
        Runtime.cleanOutput();
        return new TemporalRotationPolicy("Temporal {TIMESTAMP}.log",
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM), 2, ChronoField.SECOND_OF_MINUTE);
    }
}
