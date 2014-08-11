package de.mxro.async.map.operations;

import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.PureAsyncMap;

/**
 * An object representation of a Get operation on an asynchronous map.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 * @param <V>
 * @see PureAsyncMap
 */
public class GetOperation<K, V> {

	private final K key;
	private final ValueCallback<V> callback;

	public ValueCallback<V> getCallback() {
		return callback;
	}

	public K getKey() {
		return key;
	}

	public GetOperation(K key, ValueCallback<V> callback) {
		super();
		this.key = key;
		this.callback = callback;
	}

}
