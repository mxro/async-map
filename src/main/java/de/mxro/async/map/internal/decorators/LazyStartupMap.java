package de.mxro.async.map.internal.decorators;

import java.util.LinkedList;
import java.util.List;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.internal.Value;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;

/**
 * <P>
 * Starts up this map when a first request is received.
 * <p>
 * Stops the map only when it has been started up.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 * @param <K>
 * @param <V>
 */
public class LazyStartupMap<K, V> implements AsyncMap<K, V> {

	private static final String ERROR_MESSAGE = "Lazy start map is not started up. Needs to be started using a call to start or a call to one of its asynchronous operations.";
	final Value<Boolean> started;
	final AsyncMap<K, V> decorated;
	final List<SimpleCallback> starting;
	/**
	 * An unsynchronized variable, which allows faster access to the started
	 * state.
	 */
	final boolean started_fast_access;

	@Override
	public void put(final K key, final V value, final SimpleCallback callback) {
		if (started_fast_access) {
			decorated.put(key, value, callback);
			return;
		}

		synchronized (started) {
			if (started.get()) {
				decorated.put(key, value, callback);
				return;
			}

			start(new SimpleCallback() {

				@Override
				public void onFailure(Throwable t) {
					callback.onFailure(new Exception(
							"Delayed start could not be performed.", t));
				}

				@Override
				public void onSuccess() {
					decorated.put(key, value, callback);
				}
			});
		}

	}

	@Override
	public void get(final K key, final ValueCallback<V> callback) {
		if (started_fast_access) {
			decorated.get(key, callback);
			return;
		}

		synchronized (started) {
			if (started.get()) {
				decorated.get(key, callback);
				return;
			}

			start(new SimpleCallback() {

				@Override
				public void onFailure(Throwable t) {
					callback.onFailure(new Exception(
							"Delayed start could not be performed.", t));
				}

				@Override
				public void onSuccess() {
					decorated.get(key, callback);
				}
			});
		}

	}

	@Override
	public void remove(final K key, final SimpleCallback callback) {
		if (started_fast_access) {
			decorated.remove(key, callback);
			return;
		}

		synchronized (started) {
			if (started.get()) {
				decorated.remove(key, callback);
				return;
			}

			start(new SimpleCallback() {

				@Override
				public void onFailure(Throwable t) {
					callback.onFailure(new Exception(
							"Delayed start could not be performed.", t));
				}

				@Override
				public void onSuccess() {
					decorated.remove(key, callback);
				}
			});
		}

	}

	@Override
	public V getSync(K key) {
		if (started_fast_access) {

			return decorated.getSync(key);
		}

		synchronized (started) {
			if (started.get()) {

				return decorated.getSync(key);
			}
		}

		throw new RuntimeException(
				ERROR_MESSAGE);

	}

	@Override
	public void putSync(K key, V value) {
		if (started_fast_access) {
			decorated.putSync(key, value);
			return;
		}

		synchronized (started) {
			if (started.get()) {
				decorated.putSync(key, value);
				return;
			}
		}

		throw new RuntimeException(
				ERROR_MESSAGE);

	}

	@Override
	public void removeSync(K key) {
		if (started_fast_access) {
			decorated.removeSync(key);
			return;
		}

		synchronized (started) {
			if (started.get()) {
				decorated.removeSync(key);
				return;
			}
		}

		throw new RuntimeException(
				ERROR_MESSAGE);

	}

	@Override
	public void start(SimpleCallback callback) {
		synchronized (starting) {
			if (starting.size() > 0) {
				starting.add(callback);
				return;
			}
		}
		starting.add(callback);
		decorated.start(new SimpleCallback() {
			
			@Override
			public void onFailure(Throwable t) {
				synchronized (starting) {
					
				}
			}
			
			@Override
			public void onSuccess() {
				synchronized (started) {
					started.set(true);
				}
				
			}
		});
		
	}

	@Override
	public void stop(SimpleCallback callback) {
		if (started_fast_access) {
			decorated.stop(callback);
			return;
		}

		synchronized (started) {
			if (started.get()) {
				decorated.stop(callback);
				return;
			}
		}

		callback.onSuccess();
	}

	@Override
	public void commit(final SimpleCallback callback) {
		if (started_fast_access) {
			decorated.commit(callback);
			return;
		}

		synchronized (started) {
			if (started.get()) {
				decorated.commit(callback);
				return;
			}
		}
		
		start(new SimpleCallback() {
			
			@Override
			public void onFailure(Throwable t) {
				callback.onFailure(t);
			}
			
			@Override
			public void onSuccess() {
				decorated.commit(callback);
			}
		});
	}

	@Override
	public void performOperation(MapOperation operation) {
		if (started_fast_access) {
			decorated.performOperation(operation);
			return;
		}

		synchronized (started) {
			if (started.get()) {
				decorated.performOperation(operation);
				return;
			}
		}

		throw new RuntimeException(
				ERROR_MESSAGE);

	}

	public LazyStartupMap(AsyncMap<K, V> decorated) {
		super();
		this.started_fast_access = false;
		this.started = new Value<Boolean>(false);
		this.decorated = decorated;
		this.starting = new LinkedList<SimpleCallback>();
	}

}
