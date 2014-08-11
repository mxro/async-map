package de.mxro.async.map.jre;

import java.util.Collections;
import java.util.WeakHashMap;

import de.mxro.async.map.MapConnection;
import de.mxro.async.map.internal.decorators.MapCacheMapConnection;

public class AsyncMapsJre {

	public static final MapConnection cacheWithWeakReferences(
			MapConnection decorated) {
		return new MapCacheMapConnection(Collections
				.synchronizedMap(new WeakHashMap<String, Object>()), decorated);
	}

}
