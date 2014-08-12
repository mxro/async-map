package de.mxro.async.map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.operations.ClearCacheOperation;
import de.mxro.async.map.operations.GetOperation;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.service.Service;

public interface AsyncMap<K, V> extends Service {

	public void put(K key, V value, SimpleCallback callback);

	public void get(K key, ValueCallback<V> callback);

	public void remove(K key, SimpleCallback callback);

	public V getSync(K key);

	public void putSync(K key, V value);

	public void removeSync(K key);

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
	 * <p>
	 * Perform a generic operation on this map.
	 * <p>
	 * For example, {@link ClearCacheOperation}, {@link GetOperation}
	 */
	public void performOperation(MapOperation operation);

}
