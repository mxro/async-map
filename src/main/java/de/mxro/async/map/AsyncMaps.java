package de.mxro.async.map;

import java.util.Map;

import de.mxro.async.map.internal.decorators.DelayPutConnection;
import de.mxro.async.map.internal.decorators.LazyStartupMap;
import de.mxro.async.map.internal.decorators.MapCacheMapConnection;
import de.mxro.async.map.internal.decorators.PurgeInvalidValuesMap;
import de.mxro.concurrency.Concurrency;

public class AsyncMaps {

	public final static <K, V> AsyncMap<K, V> purgeInvalidValues(
			AsyncMap<K, V> forMap) {
		return new PurgeInvalidValuesMap<K, V>(forMap);
	}

	public static <K, V> AsyncMap<K, V> delayPutConnection(int delay,
			Concurrency concurrency, AsyncMap<K, V> decorated) {
		return new DelayPutConnection<K, V>(delay, concurrency, decorated);
	}

	public static <K, V> AsyncMap<K, V> cacheInMapConnection(
			Map<K, Object> cache, AsyncMap<K, V> decorated) {
		return new MapCacheMapConnection<K, V>(cache, decorated);
	}

	
	public static <K,V> AsyncMap<K,V> lazyStartup(AsyncMap<K,V> decorated) {
		return new LazyStartupMap<K, V>(decorated);
	}
}
