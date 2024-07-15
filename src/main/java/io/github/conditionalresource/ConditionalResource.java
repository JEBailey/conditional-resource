package io.github.conditionalresource;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Resource T is provided until a condition is met
 *
 * @param <T> resource type of the stored content
 */
public class ConditionalResource<T> {

    private final T resource;
    private final Predicate<T> predicate;

    /**
     * T which is made available based on the provided Predicate
     *
     * @param resource  to store
     * @param predicate returns true if resource can be provided
     */
    public ConditionalResource(T resource, Predicate<T> predicate) {
        Objects.requireNonNull(resource);
        this.resource = resource;
        this.predicate = predicate;
    }

    /**
     * Provide Resource of type T for the given amount of time
     *
     * @param resource  to store
     * @param lengthOfUnit anount of units to wait until expuration
     * @param unit TemporalUnit to wait
     */
    public ConditionalResource(T resource, long lengthOfUnit, TemporalUnit unit ) {
        Objects.requireNonNull(resource);
        this.resource = resource;
        var currenttime = Instant.now().plus(lengthOfUnit, unit);
        this.predicate = (r) -> {
            return Instant.now().isBefore(currenttime);
        };
    }

    /**
     * Returns the value if the predicate is true
     *
     * @return Optional value of type T
     */
    public Optional<T> get() {
        return predicate.test(resource) ? Optional.of(resource) : Optional.empty();
    }
}