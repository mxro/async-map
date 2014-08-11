package de.mxro.async.map.jre.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.MapConnection;

public class SplitWorkerThreadsMapConnection<T> implements MapConnection<T> {

	private final MapConnection<T> decorated;
	private final ExecutorService executor;
	private final ConcurrentHashMap<String, Object> pendingPuts;

	private final static Object NULL = new Object() {

	};

	@SuppressWarnings("unchecked")
	private final void writeValue(final String key, final SimpleCallback callback) {
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
				decorated.put(key, (T) value, callback);
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
	public void put(final String key, final Object value,
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
	public void get(String key, ValueCallback<T> callback) {
		Object value = pendingPuts.get(key);

		if (value != null) {
			if (value != NULL) {
				callback.onSuccess((T) value);
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
	public T getSync(String key) {
		
		Object value = pendingPuts.get(key);

		if (value != null) {
			if (value != NULL) {
				return (T) value;
			} else {
				return null;
			}
		}

		return decorated.getSync(key);
	}

	@Override
	public void remove(final String key, final SimpleCallback callback) {
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
	public void close(final SimpleCallback callback) {

		commit(new SimpleCallback() {

			@Override
			public void onFailure(Throwable t) {
				callback.onFailure(t);
			}

			@Override
			public void onSuccess() {
				decorated.close(new SimpleCallback() {

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

	public SplitWorkerThreadsMapConnection(MapConnection<T> connection,
			int workerThreads) {
		super();
		this.decorated = connection;
		this.executor = Executors.newFixedThreadPool(4);
		this.pendingPuts = new ConcurrentHashMap<String, Object>();
	}

}
