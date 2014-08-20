package de.mxro.async.map.internal.decorators;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.fn.Function;

/**
 * <P>Allows to apply a simple filter on keys before they are passed onto the
 * decorated connection.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
final class KeyFilterMap<K,V> implements AsyncMap<K, V> {

	private final Function<K, K> filter;
	private final AsyncMap<K, V> decorated;
	
	@Override
	public void put(K key, V value, SimpleCallback callback) {
		this.decorated.put(filter.apply(key), value, callback);
	}

	@Override
	public void get(K key, ValueCallback<V> callback) {
		this.decorated.get(filter.apply(key), callback);
	}

	@Override
	public void remove(K key, SimpleCallback callback) {
		this.decorated.remove(filter.apply(key), callback);
	}

	@Override
	public V getSync(K key) {
		return this.decorated.getSync(filter.apply(key));
	}

	@Override
	public void putSync(K key, V value) {
		this.decorated.putSync(filter.apply(key), value);
	}

	@Override
	public void removeSync(K key) {
		this.decorated.removeSync(filter.apply(key));
	}

	@Override
	public void start(SimpleCallback callback) {
		this.decorated.start(callback);
	}

	@Override
	public void stop(SimpleCallback callback) {
		this.decorated.stop(callback);
	}

	@Override
	public void commit(SimpleCallback callback) {
		this.decorated.commit(callback);
	}

	@Override
	public void performOperation(MapOperation operation) {
		this.decorated.performOperation(operation);
	}

	public KeyFilterMap(Function<K, K> filter, AsyncMap<K, V> decorated) {
		super();
		this.filter = filter;
		this.decorated = decorated;
	}

	
	
}
