import it.menzani.logger.api.Logger;
import it.menzani.logger.api.RotationPolicy;
import it.menzani.logger.impl.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
                        .addConsumer(new RotatingFileConsumer(Runtime.folder, createRotationPolicy())));

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
        return new StartupRotationPolicy("Startup {TIME}-{ID}.log", new TimestampFormatter(
                LocalDate::now, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }

    private static RotationPolicy newTemporalRotationPolicy() {
        Runtime.cleanOutput();
        return new TemporalRotationPolicy("Temporal {TIME}.log", new TimestampFormatter(),
                Instant::now, Instant.now().plusSeconds(2), Duration.ofSeconds(2));
    }
}
