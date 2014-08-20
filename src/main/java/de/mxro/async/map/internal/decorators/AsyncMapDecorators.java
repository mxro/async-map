package de.mxro.async.map.internal.decorators;

import java.util.Map;

import de.mxro.async.map.AsyncMap;
import de.mxro.concurrency.Concurrency;
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
	public static <K, V> AsyncMap<K, V> enforceAsynchronousPut(int delay,
			Concurrency concurrency, AsyncMap<K, V> decorated) {
		return new EnforceAsynchronousPutMap<K, V>(delay, concurrency,
				decorated);
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
	public final static <K, V> AsyncMap<K, V> purgeInvalidValues(
			AsyncMap<K, V> forMap) {
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
	public static <K, V> AsyncMap<K, V> cache(Map<K, Object> cache,
			AsyncMap<K, V> decorated) {
		return new CachedMap<K, V>(cache, decorated);
	}
	
	
	public final static <K, V> AsyncMap<K, V> filterKeys(Function<K, K> filter,
			AsyncMap<K, V> decorated) {
		return new KeyFilterMap<K, V>(filter, decorated);
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
	public static <K, V> AsyncMap<K, V> lazyStartup(AsyncMap<K, V> decorated) {
		return new LazyStartupMap<K, V>(decorated);
	}
}
