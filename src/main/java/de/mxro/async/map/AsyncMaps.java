package de.mxro.async.map;

import java.util.Map;

import de.mxro.async.map.internal.decorators.DelayPutConnection;
import de.mxro.async.map.internal.decorators.MapCacheMapConnection;
import de.mxro.async.map.internal.decorators.PurgeInvalidNodesMapConnection;
import de.mxro.concurrency.Concurrency;

public class AsyncMaps {

	public final static MapConnection purgeInvalidValues(MapConnection mapConnection) {
		return new PurgeInvalidNodesMapConnection(mapConnection);
	}

	public static MapConnection delayPutConnection(int delay, Concurrency concurrency, MapConnection decorated) {
		return new DelayPutConnection(delay, concurrency, decorated);
	}

	public static MapConnection cacheInMapConnection(Map<String, Object> cache, MapConnection decorated) {
		return new MapCacheMapConnection(cache, decorated);
	}

}
