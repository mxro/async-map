package de.mxro.async.map.operations;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.map.AsyncMap;

/**
 * An object representation of a remove operation on an {@link AsyncMap}.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
public class RemoveOperation<K> implements MapOperation {

	private final K key;
	private final SimpleCallback callback;

	public K getKey() {
		return key;
	}

	public SimpleCallback getCallback() {
		return callback;
	}

	public RemoveOperation(K key, SimpleCallback callback) {
		super();
		this.key = key;
		this.callback = callback;
	}

}
