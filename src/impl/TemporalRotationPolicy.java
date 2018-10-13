package it.menzani.logger.impl;

import it.menzani.logger.ConfigurableThreadFactory;
import it.menzani.logger.Objects;
import it.menzani.logger.StringFormat;
import it.menzani.logger.api.RotationPolicy;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TemporalRotationPolicy implements RotationPolicy, Runnable {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            ConfigurableThreadFactory.daemon("TemporalRotationPolicy daemon"));
    private final StringFormat nameFormat;
    private final Instant rotationInstant;
    private final Duration rotationPeriod;
    private volatile Path root;
    private volatile Path currentFile;

    public TemporalRotationPolicy(String nameFormat, TimestampFormatter timestampFormatter,
                                  Instant rotationInstant, Duration rotationPeriod) {
        if (Objects.objectNotNull(rotationPeriod, "rotationPeriod").isNegative() || rotationPeriod.isZero()) {
            throw new IllegalArgumentException("rotationPeriod must be positive.");
        }
        this.nameFormat = new StringFormat(Objects.objectNotNull(nameFormat, "nameFormat"))
                .evaluate("time", () -> timestampFormatter.format(null));
        this.rotationInstant = Objects.objectNotNull(rotationInstant, "rotationInstant");
        this.rotationPeriod = rotationPeriod;
    }

    @Override
    public void initialize(Path root) {
        this.root = root;
        run();
        long delay = Instant.now().until(rotationInstant, ChronoUnit.MILLIS);
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
