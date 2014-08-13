package de.mxro.async.map.jre;

import java.util.Collections;
import java.util.WeakHashMap;

import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.internal.decorators.CachedMap;
import de.mxro.async.map.jre.internal.SplitWorkerThreadsMapConnection;

public class AsyncMapsJre {

	public static final <K, V> AsyncMap<K, V> cacheWithWeakReferences(
			AsyncMap<K, V> decorated) {
		return new CachedMap<K, V>(
				Collections.synchronizedMap(new WeakHashMap<K, Object>()),
				decorated);
	}

	public static final <K, V> AsyncMap<K, V> divideWork(int workerThreads,
			AsyncMap<K, V> decorated) {
		return new SplitWorkerThreadsMapConnection<K, V>(decorated,
				workerThreads);
	}

}
