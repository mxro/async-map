package de.mxro.async.map;

import java.util.Map;

import de.mxro.async.map.internal.decorators.DelayPutMap;
import de.mxro.async.map.internal.decorators.KeyFilterMap;
import de.mxro.async.map.internal.decorators.LazyStartupMap;
import de.mxro.async.map.internal.decorators.CachedMap;
import de.mxro.async.map.internal.decorators.PurgeInvalidValuesMap;
import de.mxro.concurrency.Concurrency;
import de.mxro.fn.Function;

public class AsyncMaps {

	public final static <K, V> AsyncMap<K, V> purgeInvalidValues(
			AsyncMap<K, V> forMap) {
		return new PurgeInvalidValuesMap<K, V>(forMap);
	}

	public static <K, V> AsyncMap<K, V> delayPutConnection(int delay,
			Concurrency concurrency, AsyncMap<K, V> decorated) {
		return new DelayPutMap<K, V>(delay, concurrency, decorated);
	}

	public static <K, V> AsyncMap<K, V> cacheInMapConnection(
			Map<K, Object> cache, AsyncMap<K, V> decorated) {
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
