package de.mxro.async.map;

import java.util.Map;

import de.mxro.async.map.internal.decorators.CachedMap;
import de.mxro.async.map.internal.decorators.EnforceAsynchronousPutMap;
import de.mxro.async.map.internal.decorators.KeyFilterMap;
import de.mxro.async.map.internal.decorators.LazyStartupMap;
import de.mxro.async.map.internal.decorators.PurgeInvalidValuesMap;
import de.mxro.concurrency.Concurrency;
import de.mxro.fn.Function;

public class AsyncMaps {

	public final static <K, V> AsyncMap<K, V> purgeInvalidValues(
			AsyncMap<K, V> forMap) {
		return new PurgeInvalidValuesMap<K, V>(forMap);
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
	public static <K, V> AsyncMap<K, V> enforceAsynchronousPut(int delay,
			Concurrency concurrency, AsyncMap<K, V> decorated) {
		return new EnforceAsynchronousPutMap<K, V>(delay, concurrency,
				decorated);
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
	 * 
	 * @param decorated
	 * @return
	 * @see LazyStartupMap
	 */
	public static <K, V> AsyncMap<K, V> lazyStartup(AsyncMap<K, V> decorated) {
		return new LazyStartupMap<K, V>(decorated);
	}
}
