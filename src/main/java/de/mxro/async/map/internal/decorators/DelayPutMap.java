package de.mxro.async.map.internal.decorators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.flow.CallbackLatch;
import de.mxro.async.internal.Value;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.async.map.operations.PutOperation;
import de.mxro.concurrency.Concurrency;
import de.mxro.concurrency.SimpleTimer;

public class DelayPutMap<K, V> implements AsyncMap<K, V> {

	private final AsyncMap<K, V> decorated;
	private final int delay;
	private final Concurrency concurrency;
	private final Map<K, List<PutOperation<K, V>>> pendingPuts;
	private Value<Boolean> timerActive = new Value<Boolean>(false);
	private SimpleTimer timer = null;

	private final static SimpleCallback EMPTY_CALLBACK = new SimpleCallback() {

		@Override
		public void onFailure(Throwable arg0) {

		}

		@Override
		public void onSuccess() {

		}
	};

	/*
	 * private final static Object NULL = new Object() {
	 * 
	 * };
	 */

	@Override
	public void put(K key, V value, SimpleCallback callback) {
		synchronized (pendingPuts) {

			if (!pendingPuts.containsKey(key)) {
				pendingPuts.put(key, new LinkedList<PutOperation<K, V>>());
			}

			final PutOperation<K, V> putOperation = new PutOperation<K, V>(key,
					value, callback);

			pendingPuts.get(key).add(putOperation);

		}

		synchronized (timerActive) {
			if (timerActive.get() == true) {
				return;
			}

			timerActive.set( true);

			timer = concurrency.newTimer().scheduleOnce(delay, new Runnable() {

				@Override
				public void run() {

					synchronized (timerActive) {
						timerActive.set( false);
						timer = null;
					}
					processPuts(EMPTY_CALLBACK);
				}
			});

		}
	}

	private final void processPuts(final SimpleCallback callback) {
		final Map<K, List<PutOperation<K, V>>> puts;
		synchronized (pendingPuts) {
			puts = new HashMap<K, List<PutOperation<K, V>>>(pendingPuts);

			pendingPuts.clear();
		}

		final CallbackLatch latch = new CallbackLatch(puts.size()) {

			@Override
			public void onFailed(Throwable t) {
				callback.onFailure(t);
			}

			@Override
			public void onCompleted() {
				callback.onSuccess();
			}
		};
		for (final Entry<K, List<PutOperation<K, V>>> put : puts.entrySet()) {

			decorated.put(put.getKey(),
					put.getValue().get(put.getValue().size() - 1).getValue(),
					new SimpleCallback() {

						@Override
						public void onFailure(Throwable arg0) {
							for (PutOperation<K, V> operation : put.getValue()) {
								operation.getCallback().onFailure(arg0);
							}
							latch.registerSuccess();
						}

						@Override
						public void onSuccess() {
							for (PutOperation<K, V> operation : put.getValue()) {
								operation.getCallback().onSuccess();
							}
							latch.registerSuccess();
						}
					});
		}

	}

	@Override
	public void get(K key, ValueCallback<V> callback) {
		synchronized (pendingPuts) {
			if (pendingPuts.containsKey(key)) {
				callback.onSuccess(pendingPuts.get(key)
						.get(pendingPuts.get(key).size() - 1).getValue());
				return;
			}
		}
		decorated.get(key, callback);
	}

	@Override
	public V getSync(K key) {
		synchronized (pendingPuts) {
			if (pendingPuts.containsKey(key)) {
				return pendingPuts.get(key)
						.get(pendingPuts.get(key).size() - 1).getValue();
			}
		}

		return decorated.getSync(key);
	}

	@Override
	public void start(SimpleCallback callback) {
		decorated.start(callback);
	}

	@Override
	public void putSync(K key, V value) {
		throw new RuntimeException(
				"Synchronized put not supported on delayed put connection.");
	}

	@Override
	public void removeSync(K key) {

		synchronized (pendingPuts) {
			pendingPuts.remove(key);
		}

		decorated.removeSync(key);
	}

	@Override
	public void remove(K key, SimpleCallback callback) {
		synchronized (pendingPuts) {
			pendingPuts.remove(key);
		}
		decorated.remove(key, callback);
	}

	private final void processAllPuts(final SimpleCallback callback) {
		processPuts(new SimpleCallback() {

			@Override
			public void onFailure(Throwable arg0) {
				callback.onFailure(arg0);
			}

			@Override
			public void onSuccess() {
				synchronized (pendingPuts) {
					if (pendingPuts.size() == 0) {
						callback.onSuccess();
						return;
					}
				}

				processAllPuts(callback);

			}
		});
	}

	@Override
	public void stop(final SimpleCallback callback) {
		processAllPuts(new SimpleCallback() {

			@Override
			public void onFailure(Throwable arg0) {
				callback.onFailure(arg0);
			}

			@Override
			public void onSuccess() {
				synchronized (timerActive) {
					if (timerActive.get() == true) {
						timer.stop();
					}
				}
				decorated.stop(callback);
			}
		});

	}

	@Override
	public void commit(final SimpleCallback callback) {
		processAllPuts(new SimpleCallback() {

			@Override
			public void onFailure(Throwable arg0) {
				callback.onFailure(arg0);
			}

			@Override
			public void onSuccess() {
				decorated.commit(callback);
			}
		});

	}

	

	@Override
	public void performOperation(MapOperation operation) {
		decorated.performOperation(operation);
	}

	public DelayPutMap(int delay, Concurrency con,
			AsyncMap<K, V> decorated) {
		super();
		this.decorated = decorated;
		this.delay = delay;
		this.concurrency = con;
		this.pendingPuts = new HashMap<K, List<PutOperation<K, V>>>();
	}

}
