package de.mxro.async.map.internal.decorators;

import java.util.Map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.fn.Fn;

class CachedMap<K, V> implements AsyncMap<K, V> {

	private final AsyncMap<K, V> decorated;
	private final Map<K, Object> cache;

	private final static Object NULL = Fn.object();

	@Override
	public void put(K key, V value, SimpleCallback callback) {
		if (value != null) {
			this.cache.put(key, value);
		} else {
			this.cache.put(key, NULL);
		}
		decorated.put(key, value, callback);
	}

	@Override
	public void putSync(K key, V value) {
		if (value != null) {
			this.cache.put(key, value);
		} else {
			this.cache.put(key, NULL);
		}

		decorated.putSync(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void get(K key, ValueCallback<V> callback) {
		Object fromCache = this.cache.get(key);
		if (fromCache != null) {
			if (fromCache == NULL) {
				callback.onSuccess(null);
				return;
			} else {
				callback.onSuccess((V) fromCache);
				return;
			}

		}

		decorated.get(key, callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V getSync(K key) {
		Object fromCache = this.cache.get(key);
		if (fromCache != null) {
			if (fromCache == NULL) {
				return null;
			} else {
				return (V) fromCache;
			}

		}

		return decorated.getSync(key);
	}

	@Override
	public void remove(K key, SimpleCallback callback) {
		this.cache.remove(key);
		this.decorated.remove(key, callback);
	}

	@Override
	public void removeSync(K key) {
		this.cache.remove(key);
		this.decorated.removeSync(key);
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

	public CachedMap(Map<K, Object> cache, AsyncMap<K, V> decorated) {
		super();
		this.decorated = decorated;
		this.cache = cache;
	}

}
