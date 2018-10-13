import it.menzani.logger.api.Logger;
import it.menzani.logger.api.RotationPolicy;
import it.menzani.logger.impl.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RotatingFileConsumerTest {
    private static final Path runtimeFolder = Paths.get("runtime");

    public static void main(String[] args) throws IOException {
        Logger logger = new SynchronousLogger()
                .addPipeline(new Pipeline()
                        .setProducer(new Producer()
                                .append(new TimestampFormatter())
                                .append(" -> ")
                                .append(new MessageFormatter()))
                        .addConsumer(new ConsoleConsumer())
                        .addConsumer(new RotatingFileConsumer(runtimeFolder, newTemporalRotationPolicy())));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> logger.info(UUID.randomUUID()), 0, 500, TimeUnit.MILLISECONDS);
    }

    private static RotationPolicy newStartupRotationPolicy() {
        return new StartupRotationPolicy("Startup {TIME}-{ID}.log", new TimestampFormatter(
                LocalDate::now, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }

    private static RotationPolicy newTemporalRotationPolicy() throws IOException {
        cleanOutput();
        return new TemporalRotationPolicy("Temporal {TIME}.log", new TimestampFormatter(), Instant.now().plusSeconds(2), Duration.ofSeconds(2));
    }

    private static void cleanOutput() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(runtimeFolder, "*.log")) {
            for (Path path : stream) {
                Files.delete(path);
            }
        }
    }
}
