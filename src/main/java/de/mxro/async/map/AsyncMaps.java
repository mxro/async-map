package de.mxro.async.map;

import java.util.Map;

import de.mxro.async.map.internal.decorators.DelayPutConnection;
import de.mxro.async.map.internal.decorators.MapCacheMapConnection;
import de.mxro.async.map.internal.decorators.PurgeInvalidValuesMap;
import de.mxro.concurrency.Concurrency;

public class AsyncMaps {

	public final static <K, V> AsyncMap<K, V> purgeInvalidValues(
			AsyncMap<K, V> forMap) {
		return new PurgeInvalidValuesMap<K, V>(forMap);
	}

	public static <K, V> PersistedMap<K, V> delayPutConnection(int delay,
			Concurrency concurrency, PersistedMap<K, V> decorated) {
		return new DelayPutConnection<K, V>(delay, concurrency, decorated);
	}

	public static MapConnection cacheInMapConnection(Map<String, Object> cache,
			MapConnection decorated) {
		return new MapCacheMapConnection(cache, decorated);
	}

}
