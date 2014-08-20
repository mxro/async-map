package de.mxro.async.map.internal.decorators;

import de.mxro.async.map.AsyncMap;
import de.mxro.concurrency.Concurrency;

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
	
}
