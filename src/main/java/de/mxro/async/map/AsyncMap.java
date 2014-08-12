package de.mxro.async.map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.service.Service;

public interface AsyncMap<K, V> extends Service, AsyncMap2<K, V> {

	public void put(K key, V value, SimpleCallback callback);

	public void get(K key, ValueCallback<V> callback);

	public void remove(K key, SimpleCallback callback);
	
	/**
	 * Assures that a connection to the persistence medium is established and
	 * the map is operational.
	 * 
	 * @param callback
	 */
	public void start(SimpleCallback callback);

	/**
	 * <p>
	 * Releases resources held by this map.
	 * <p>
	 * Assures pending changes are written to persistence medium.
	 * 
	 * @param callback
	 */
	public void stop(SimpleCallback callback);

	/**
	 * Assures pending changes are written to persistence medium.
	 * 
	 * @param callback
	 */
	public void commit(SimpleCallback callback);

	/**
	 * If the persistence implementation supports a local cache, calling this
	 * method clears this cache.
	 */
	public void clearCache();

}
