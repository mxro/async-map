package de.mxro.async.map.operations;

import de.mxro.async.map.AsyncMap;

/**
 * An object representation of a remove operation on an {@link AsyncMap}.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
public class RemoveOperation {

	private final String keyOfEntryToRemove;

	public String getKeyOfEntryToRemove() {
		return keyOfEntryToRemove;
	}

	public RemoveOperation(String keyOfEntryToRemove) {
		super();
		this.keyOfEntryToRemove = keyOfEntryToRemove;
	}

}
