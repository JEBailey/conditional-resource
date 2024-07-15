package io.github.conditionalresource;

import java.util.MissingResourceException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides a resource of type T from the {@link ConditionalResource } until it's internal condition is met.
 * Then a new ConditionalResource is obtained from the Supplier and cached.
 *
 * @param <T> type of stored resource
 */
public class ConditionalResourceFactory<T> implements Supplier<T> {

    private final Supplier<ConditionalResource<T>> supplier;
    private volatile ConditionalResource<T> resource;

    /**
     * Wrapper for  {@link ConditionalResource} and caches it until the resource is no longer available.
     * Then a new ConditionalResource is obtained from the Supplier
     *
     * @param supplier of OptionalResource
     */
    public ConditionalResourceFactory(Supplier<ConditionalResource<T>> supplier) {
        this.supplier = supplier;
        this.resource = supplier.get();
    }

    /**
     * Returns the resource defined in the {@code ConditionalResource}
     * If the resource is no longer available, attempt to get the next ConditionalResource
     * from the supplier.
     * <br><br>
     * Throws a {@link MissingResourceException} if we are no longer able to generate a Resource
     *
     * @return current resource value
     */
    public synchronized T get() {
        Optional<T> item = resource.get();
        if (item.isEmpty()) {
            resource = supplier.get();
            item = resource.get();
            if (item.isEmpty()) {
                //something bad happened
                throw new MissingResourceException("unable to create new resource", item.getClass().getName(), null);
            }
        }
        return item.get();
    }
}
