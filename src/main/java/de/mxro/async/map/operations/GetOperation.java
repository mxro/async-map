package de.mxro.async.map.operations;

import de.mxro.async.callbacks.ValueCallback;

/**
 * An object representation of a Get operation on an asynchronous map.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 * @param <T>
 */
public class GetOperation<T> {

	private final ValueCallback<T> callback;

	public ValueCallback<T> getCallback() {
		return callback;
	}

	public GetOperation(ValueCallback<T> callback) {
		super();
		this.callback = callback;
	}

}
