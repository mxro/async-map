package de.mxro.async.map.internal.decorators;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.MapConnection;

/**
 * Use in case seralizations can get outdated with an update in a client cache.
 * 
 * @author mxro
 * 
 */
public final class PurgeInvalidNodesMapConnection<K, V> implements AsyncMap<K, V> {

	private final AsyncMap<K, V> decorated;

	@Override
	public void put(K key, V value, SimpleCallback callback) {
		decorated.put(key, value, callback);
	}

	@Override
	public void get(K key, ValueCallback<V> callback) {
		get(key, callback, true);
	}

	
	
	private final void get(K key, ValueCallback<V> callback, boolean firstTry) {
		try {
			decorated.get(key, callback);		
		} catch (Throwable t) {
			if (!firstTry) {
				throw new RuntimeException(t);
			}

			deleteAndReget(key, callback);
		}

	}

	private final void deleteAndReget(final K key, final ValueCallback<V> callback) {
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
	public V getSync(K key) {
		try {
			V res = decorated.getSync(key);
			
			if (res == null) {
				return removeAndRegetSync(key);
			}
			
			return res;
		} catch (Throwable t) {
			return removeAndRegetSync(key);
		}
		
	}

	public V removeAndRegetSync(K key) {
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
	public void remove(K key, SimpleCallback callback) {
		decorated.remove(key, callback);
	}

	

	public PurgeInvalidNodesMapConnection(AsyncMap<K, V> decorated) {
		super();
		this.decorated = decorated;
	}

}
