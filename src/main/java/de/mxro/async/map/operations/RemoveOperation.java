package de.mxro.async.map.operations;

import de.mxro.async.map.PureAsyncMap;

/**
 * An object representation of a remove operation on an {@link PureAsyncMap}.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
public class RemoveOperation<K> {

	private final K key;

	public K getKey() {
		return key;
	}

	public RemoveOperation(K key) {
		super();
		this.key = key;
	}

}
