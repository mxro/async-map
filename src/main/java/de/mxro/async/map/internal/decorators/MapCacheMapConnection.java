package de.mxro.async.map.internal.decorators;

import java.util.Map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.MapConnection;

public class MapCacheMapConnection<T> implements MapConnection<T> {

	private final MapConnection<T> decorated;
	private final Map<String, Object> cache;

	private final static Object NULL = new Object() {

	};

	@Override
	public void put(String key, T value, SimpleCallback callback) {
		if (value != null) {
			this.cache.put(key, value);
		} else {
			this.cache.put(key, NULL);
		}
		decorated.put(key, value, callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void get(String key, ValueCallback<T> callback) {
		Object fromCache = this.cache.get(key);
		if (fromCache != null) {
			if (fromCache == NULL) {
				callback.onSuccess(null);
				return ;
			} else {
				callback.onSuccess((T) fromCache);
				return;
			}

		}

		decorated.get(key, callback);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public T getSync(String key) {
		Object fromCache = this.cache.get(key);
		if (fromCache != null) {
			if (fromCache == NULL) {
				return null;
			} else {
				return (T) fromCache;
			}

		}

		return decorated.getSync(key);
	}

	@Override
	public void remove(String key, SimpleCallback callback) {
		this.cache.remove(key);
		this.decorated.remove(key, callback);
	}

	@Override
	public void close(SimpleCallback callback) {
		this.decorated.close(callback);
	}

	@Override
	public void commit(SimpleCallback callback) {
		this.decorated.commit(callback);
	}

	@Override
	public void clearCache() {
		this.decorated.clearCache();
	}

	public MapCacheMapConnection(Map<String, Object> cache, MapConnection<T> decorated ) {
		super();
		this.decorated = decorated;
		this.cache = cache;
	}

}
