package de.mxro.async.map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;

/**
 * <p>
 * A map for which values can only be retrieved and set using an asynchronous
 * interface.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 * @param <K>
 * @param <V>
 */
public interface PureAsyncMap<K, V> {

	public void put(K key, V value, SimpleCallback callback);

	public void get(K key, ValueCallback<V> callback);

	public void remove(K key, SimpleCallback callback);

}
