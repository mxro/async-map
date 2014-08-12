package de.mxro.async.map.jre.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.MapConnection;
import de.mxro.async.map.PersistedMap;
import de.mxro.fn.Fn;

public class SplitWorkerThreadsMapConnection<K, V> implements PersistedMap<K, V> {

	private final PersistedMap<K,V> decorated;
	private final ExecutorService executor;
	private final ConcurrentHashMap<K, Object> pendingPuts;

	private final static Object NULL = Fn.object();

	@SuppressWarnings("unchecked")
	private final void writeValue(final K key, final SimpleCallback callback) {
		synchronized (pendingPuts) {
			Object value = pendingPuts.get(key);
			if (value == null) {
				callback.onSuccess();
				return;
			}
			
			if (!pendingPuts.remove(key, value)) {
				// do nothing, someone has inserted an updated value.
				
				//throw new IllegalStateException(
				//		"Inserted key could not be removed. Key: "+key+" Value: "+value);
			}
			
			if (value != NULL) {
				decorated.put(key, (V) value, callback);
			} else {
				decorated.remove(key, new SimpleCallback() {

					@Override
					public void onFailure(Throwable t) {
						callback.onFailure(t);
					}

					@Override
					public void onSuccess() {
						callback.onSuccess();
					}
				});
			}
			
		}

	}

	@Override
	public void put(final K key, final V value,
			final SimpleCallback callback) {
		
		// System.out.println("put "+key);
		if (value != null) {
			pendingPuts.put(key, value);
		} else {
			pendingPuts.put(key, NULL);
		}

		executor.execute(new Runnable() {

			@Override
			public void run() {
				writeValue(key, callback);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void get(K key, ValueCallback<V> callback) {
		Object value = pendingPuts.get(key);

		if (value != null) {
			if (value != NULL) {
				callback.onSuccess((V) value);
				return;
			} else {
				callback.onSuccess(null);
				return;
			}
		}

		decorated.get(key, callback);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public V getSync(K key) {
		
		Object value = pendingPuts.get(key);

		if (value != null) {
			if (value != NULL) {
				return (V) value;
			} else {
				return null;
			}
		}

		return decorated.getSync(key);
	}

	@Override
	public void remove(final K key, final SimpleCallback callback) {
		pendingPuts.put(key, NULL);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				writeValue(key, new SimpleCallback() {

					@Override
					public void onFailure(Throwable t) {
						callback.onFailure(t);
					}

					@Override
					public void onSuccess() {
						callback.onSuccess();
					}
				});
			}
		});
	}

	@Override
	public void stop(final SimpleCallback callback) {

		commit(new SimpleCallback() {

			@Override
			public void onFailure(Throwable t) {
				callback.onFailure(t);
			}

			@Override
			public void onSuccess() {
				decorated.stop(new SimpleCallback() {

					@Override
					public void onFailure(Throwable t) {
						callback.onFailure(t);
					}

					@Override
					public void onSuccess() {
						new Thread() {

							@Override
							public void run() {
								executor.shutdown();

								try {
									executor.awaitTermination(60000,
											TimeUnit.MILLISECONDS);
								} catch (InterruptedException e) {
									callback.onFailure(e);
									return;
								}
								callback.onSuccess();

							}

						}.start();
					}
				});
			}
		});

	}

	@Override
	public void commit(final SimpleCallback callback) {

		new Thread() {

			@Override
			public void run() {

				while (pendingPuts.size() > 0) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						callback.onFailure(e);
						return;
					}
				}

				decorated.commit(callback);
			}

		}.start();

	}

	@Override
	public void clearCache() {
		decorated.clearCache();
	}

	public SplitWorkerThreadsMapConnection(PersistedMap<K,V> connection,
			int workerThreads) {
		super();
		this.decorated = connection;
		this.executor = Executors.newFixedThreadPool(4);
		this.pendingPuts = new ConcurrentHashMap<K, Object>();
	}

}
