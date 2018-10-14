package it.menzani.logger.impl;

import it.menzani.logger.ConfigurableThreadFactory;
import it.menzani.logger.Objects;
import it.menzani.logger.StringFormat;
import it.menzani.logger.api.RotationPolicy;

import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// FIXME Always switches on startup.
// FIXME Switch time is not precise.
@Deprecated
public final class TemporalRotationPolicy implements RotationPolicy, Runnable {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            ConfigurableThreadFactory.daemon("TemporalRotationPolicy daemon"));
    private final StringFormat nameFormat;
    private final TimestampFormatter.Clock clock;
    private final Temporal rotationInstant;
    private final Duration rotationPeriod;
    private volatile Path root;
    private volatile Path currentFile;

    public TemporalRotationPolicy(String nameFormat, TimestampFormatter timestampFormatter,
                                  TimestampFormatter.Clock clock, Temporal rotationInstant, Duration rotationPeriod) {
        this.nameFormat = new StringFormat(Objects.objectNotNull(nameFormat, "nameFormat"))
                .evaluate("time", () -> timestampFormatter.format(null));
        this.clock = Objects.objectNotNull(clock, "clock");
        this.rotationInstant = Objects.objectNotNull(rotationInstant, "rotationInstant");
        if (Objects.objectNotNull(rotationPeriod, "rotationPeriod").isNegative() || rotationPeriod.isZero()) {
            throw new IllegalArgumentException("rotationPeriod must be positive.");
        }
        this.rotationPeriod = rotationPeriod;
    }

    @Override
    public void initialize(Path root) throws Exception {
        this.root = root;
        run();
        long delay = clock.now().until(rotationInstant, ChronoUnit.MILLIS);
        executor.scheduleAtFixedRate(this, delay, rotationPeriod.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            currentFile = root.resolve(nameFormat.clone().evaluateToString());
        } catch (Exception e) {
            throw new RotationException(e);
        }
    }

    @Override
    public Path currentFile() {
        return currentFile;
    }
}
