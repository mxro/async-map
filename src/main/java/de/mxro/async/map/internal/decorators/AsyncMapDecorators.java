package de.mxro.async.map.internal.decorators;

import java.util.Map;

import de.mxro.async.map.AsyncMap;
import de.mxro.concurrency.Concurrency;
import de.mxro.fn.Closure;
import de.mxro.fn.Function;

public class AsyncMapDecorators {

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
        return new EnforceAsynchronousPutMap<K, V>(delay, concurrency, decorated);
    }

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
        return new PurgeInvalidValuesMap<K, V>(forMap);
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
        return new SimpleCachedMap<K, V>(cache, decorated);
    }

    /**
     * <p>
     * Caches writes to this map in another map.
     * 
     * @param primaryCache
     * @param secondaryCache
     * @return
     */
    public static <K, V> AsyncMap<K, V> tierCaches(final AsyncMap<K, V> primaryCache,
            final AsyncMap<K, V> secondaryCache) {
        return new TieredCachesMap<K, V>(primaryCache, secondaryCache);
    }

    public final static <K, V> AsyncMap<K, V> filterKeys(final Function<K, K> filter, final AsyncMap<K, V> decorated) {
        return new KeyFilterMap<K, V>(filter, decorated);
    }

    public final static <K, V> AsyncMap<K, V> filterValues(final Function<V, V> filter, final AsyncMap<K, V> decorated) {
        return new ValueFilterMap<K, V>(filter, decorated);
    }

    public final static <K, V> AsyncMap<K, V> ignoreKeys(final Function<K, Boolean> filter,
            final AsyncMap<K, V> decorated) {
        return new IgnoreKeysMap<K, V>(filter, decorated);
    }

    /**
     * <p>
     * Calls the {@link AsyncMap#start(de.mxro.async.callbacks.SimpleCallback)}
     * method of this map automatically when an asynchronous operation is
     * called.
     * 
     * @param decorated
     * @return
     * @see LazyStartupMap
     */
    public static <K, V> AsyncMap<K, V> lazyStartup(final AsyncMap<K, V> decorated) {
        return new LazyStartupMap<K, V>(decorated);
    }

    public static <K, V> AsyncMap<K, V> trace(final Closure<String> messageReceiver, final AsyncMap<K, V> decorated) {
        return new TraceMap<K, V>(messageReceiver, decorated);
    }
}
