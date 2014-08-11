package de.mxro.async.map.internal.decorators;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.PureAsyncMap;
import de.mxro.async.map.MapConnection;
import de.mxro.async.map.PureSyncMap;

/**
 * Use in case seralizations can get outdated with an update in a client cache.
 * 
 * @author mxro
 * 
 */
public final class PurgeInvalidNodesMapConnection<K, V> implements PureAsyncMap<K, V>, PureSyncMap<K, V> {

	private final MapConnection<T> decorated;

	@Override
	public void put(String key, T value, SimpleCallback callback) {
		decorated.put(key, value, callback);
	}

	@Override
	public void get(String key, ValueCallback<T> callback) {
		get(key, callback, true);
	}

	
	
	private final void get(String key, ValueCallback<T> callback, boolean firstTry) {
		try {
			decorated.get(key, callback);		
		} catch (Throwable t) {
			if (!firstTry) {
				throw new RuntimeException(t);
			}

			deleteAndReget(key, callback);
		}

	}

	private final void deleteAndReget(final String key, final ValueCallback<T> callback) {
		remove(key, new SimpleCallback() {

			@Override
			public void onFailure(Throwable t) {
				callback.onFailure(t);
			}

			@Override
			public void onSuccess() {
				get(key, callback, false);
			}
		});

		 
	}


	@Override
	public T getSync(String key) {
		try {
			T res = decorated.getSync(key);
			
			if (res == null) {
				return removeAndRegetSync(key);
			}
			
			return res;
		} catch (Throwable t) {
			return removeAndRegetSync(key);
		}
		
	}

	public T removeAndRegetSync(String key) {
		remove(key, new SimpleCallback() {
			
			@Override
			public void onFailure(Throwable t) {
				throw new RuntimeException(t);
			}
			
			@Override
			public void onSuccess() {
				
			}
		});
		
		return decorated.getSync(key);
	}
	
	@Override
	public void remove(String key, SimpleCallback callback) {
		decorated.remove(key, callback);
	}

	@Override
	public void close(SimpleCallback callback) {
		decorated.close(callback);
	}

	@Override
	public void commit(SimpleCallback callback) {
		decorated.commit(callback);
	}

	@Override
	public void clearCache() {
		decorated.clearCache();
	}

	public PurgeInvalidNodesMapConnection(MapConnection decorated) {
		super();
		this.decorated = decorated;
	}

}
