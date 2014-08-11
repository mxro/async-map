package de.mxro.async.map.operations;

import de.mxro.async.callbacks.ValueCallback;

/**
 * An object representation of a Get operation on an asynchronous map.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 * @param <V>
 */
public class GetOperation<V> {

	private final ValueCallback<V> callback;

	public ValueCallback<V> getCallback() {
		return callback;
	}

	public GetOperation(ValueCallback<V> callback) {
		super();
		this.callback = callback;
	}

}
