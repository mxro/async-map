package de.mxro.async.map;

import java.util.Map;

import de.mxro.async.map.internal.HashMapAsyncMap;
import de.mxro.async.map.internal.decorators.AsyncMapDecorators;
import de.mxro.concurrency.Concurrency;
import delight.functional.Closure;

public class AsyncMaps {

    /**
     * <p>
     * If values cannot be read from the map for some reason, they are
     * automatically deleted in the map.
     * <p>
     * This can be useful for example if the serialization format of objects has
     * changed and the map only works as a cache.
     * 
     * @param forMap
     * @return
     */
    public final static <K, V> AsyncMap<K, V> purgeInvalidValues(final AsyncMap<K, V> forMap) {
        return AsyncMapDecorators.purgeInvalidValues(forMap);
    }

    /**
     * <p>
     * Enforces that even putSync operations are performed asynchronously in the
     * background.
     * <p>
     * This is not visible to the caller though (putSync returns immediately).
     * 
     * @param delay
     * @param concurrency
     * @param decorated
     * @return
     */
    public static <K, V> AsyncMap<K, V> enforceAsynchronousPut(final int delay, final Concurrency concurrency,
            final AsyncMap<K, V> decorated) {
        return AsyncMapDecorators.enforceAsynchronousPut(delay, concurrency, decorated);
    }

    /**
     * <p>
     * Caches writes to this map in a Java {@link Map} object and performs reads
     * from this cache whenever possible.
     * 
     * @param cache
     * @param decorated
     * @return
     */
    public static <K, V> AsyncMap<K, V> cache(final Map<K, Object> cache, final AsyncMap<K, V> decorated) {
        return AsyncMapDecorators.cache(cache, decorated);
    }

    public static <K, V> AsyncMap<K, V> tierCaches(final AsyncMap<K, V> primaryCache,
            final AsyncMap<K, V> secondaryCache) {
        return AsyncMapDecorators.tierCaches(primaryCache, secondaryCache);
    }

    public final static <K, V> AsyncMap<K, V> filterKeys(final Function<K, K> filter, final AsyncMap<K, V> decorated) {
        return AsyncMapDecorators.filterKeys(filter, decorated);
    }

    public final static <K, V> AsyncMap<K, V> filterValues(final Function<V, V> beforeStorage,
            final Function<V, V> afterStorage, final AsyncMap<K, V> decorated) {
        return AsyncMapDecorators.filterValues(beforeStorage, afterStorage, decorated);
    }

    public final static <K, V> AsyncMap<K, V> ignoreKeys(final Function<K, Boolean> filter,
            final AsyncMap<K, V> decorated) {
        return AsyncMapDecorators.ignoreKeys(filter, decorated);
    }

    /**
     * <p>
     * Calls the {@link AsyncMap#start(de.mxro.async.callbacks.SimpleCallback)}
     * method of this map automatically when an asynchronous operation is
     * called.
     * 
     * @return
     * @see LazyStartupMap
     */
    public static <K, V> AsyncMap<K, V> lazyStartup(final AsyncMap<K, V> decorated) {
        return AsyncMapDecorators.lazyStartup(decorated);
    }

    public static <K, V> AsyncMap<K, V> hashMap() {
        return new HashMapAsyncMap<K, V>();
    }

    public static <K, V> AsyncMap<K, V> trace(final Closure<String> messageReceiver, final AsyncMap<K, V> decorated) {
        return AsyncMapDecorators.trace(messageReceiver, decorated);
    }

}
