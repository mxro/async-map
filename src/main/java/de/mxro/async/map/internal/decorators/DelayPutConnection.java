package de.mxro.async.map.internal.decorators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.flow.CallbackLatch;
import de.mxro.async.map.MapConnection;
import de.mxro.concurrency.Concurrency;
import de.mxro.concurrency.SimpleTimer;

public class DelayPutConnection<T> implements MapConnection<T> {

	private final MapConnection<T> decorated;
	private final int delay;
	private final Concurrency concurrency;
	private final Map<String, PendingPut<T>> pendingPuts;
	private Boolean timerActive = false;
	private SimpleTimer timer = null;

	private final static class PendingPut<T> {
		public T obj;
		public List<SimpleCallback> callback = new LinkedList<SimpleCallback>();
	}

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
	public void put(String key, Object value, SimpleCallback callback) {
		synchronized (pendingPuts) {

			if (pendingPuts.containsKey(key)) {
				PendingPut put = pendingPuts.get(key);
				put.obj = value;
				put.callback.add(callback);
			} else {
				PendingPut put = new PendingPut();
				put.obj = value;
				put.callback.add(callback);
				pendingPuts.put(key, put);
			}
		}

		synchronized (timerActive) {
			if (timerActive == true) {
				return;
			}

			timerActive = true;

			timer = concurrency.newTimer().scheduleOnce(delay, new Runnable() {

				@Override
				public void run() {

					synchronized (timerActive) {
						timerActive = false;
						timer = null;
					}
					processPuts(EMPTY_CALLBACK);
				}
			});

		}
	}

	private final class MyEntry<K, V> implements Map.Entry<K, V> {
	    private final K key;
	    private V value;

	    public MyEntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
	
	private final void processPuts(final SimpleCallback callback) {
		final ArrayList<Entry<String, PendingPut<T>>> puts;
		synchronized (pendingPuts) {
			Set<Entry<String, PendingPut<T>>> entries = pendingPuts.entrySet();
			puts = new ArrayList<Entry<String, PendingPut<T>>>(
					entries.size());
			for (Entry<String, PendingPut<T>> e: entries) {
				puts.add(new MyEntry<String, PendingPut<T>>(e.getKey(), e.getValue()));
			}
			
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
		for (final Entry<String, PendingPut<T>> put : puts) {

			decorated.put(put.getKey(), put.getValue().obj, new SimpleCallback() {

				@Override
				public void onFailure(Throwable arg0) {
					for (SimpleCallback callback:put.getValue().callback) {
						callback.onFailure(arg0);
					}
					latch.registerSuccess();
				}

				@Override
				public void onSuccess() {
					// System.out.println("trigger callbacks "+put.getValue().callback.size());
					for (SimpleCallback callback:put.getValue().callback) {
						callback.onSuccess();
					}
					latch.registerSuccess();
				}
			});
		}

	}

	@Override
	public void get(String key, ValueCallback<T> callback) {
		synchronized (pendingPuts) {
			if (pendingPuts.containsKey(key)) {
				callback.onSuccess(pendingPuts.get(key).obj);
				return;
			}
		}
		decorated.get(key, callback);
	}

	@Override
	public T getSync(String key) {
		synchronized (pendingPuts) {
			if (pendingPuts.containsKey(key)) {
				return pendingPuts.get(key).obj;
			}
		}

		return decorated.getSync(key);
	}

	@Override
	public void remove(String key, SimpleCallback callback) {
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
	public void close(final SimpleCallback callback) {
		processAllPuts(new SimpleCallback() {

			@Override
			public void onFailure(Throwable arg0) {
				callback.onFailure(arg0);
			}

			@Override
			public void onSuccess() {
				synchronized (timerActive) {
					if (timerActive == true) {
						timer.stop();
					}
				}
				decorated.close(callback);
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
	public void clearCache() {
		decorated.clearCache();
	}

	public DelayPutConnection(int delay, Concurrency con,
			MapConnection<T> decorated) {
		super();
		this.decorated = decorated;
		this.delay = delay;
		this.concurrency = con;
		this.pendingPuts = new HashMap<String, PendingPut<T>>();
	}

}
