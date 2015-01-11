package de.mxro.async.map.jre.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import de.mxro.async.Operation;
import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.jre.AsyncJre;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.internal.decorators.SimpleCallbackWrapper;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.fn.Fn;
import de.mxro.fn.Success;

public final class SplitWorkerThreadsMapConnection<K, V> implements AsyncMap<K, V> {

    private final boolean ENABLE_LOG = false;

    private final AsyncMap<K, V> decorated;
    private ExecutorService executor;
    private ConcurrentHashMap<K, Object> pendingPuts;
    private final int workerThreads;

    private final static Object NULL = Fn.object();

    @SuppressWarnings("unchecked")
    private final void writeValue(final K key, final SimpleCallback callback) {
        if (ENABLE_LOG) {
            System.out.println(this + ": Writing value " + key);
        }

        final Object value = pendingPuts.get(key);
        if (value == null) {
            callback.onSuccess();
            return;
        }

        if (!pendingPuts.remove(key, value)) {
            // do nothing, someone has inserted an updated value.

            callback.onSuccess();
            return;
        }

        if (value != NULL) {
            decorated.put(key, (V) value, callback);
        } else {
            decorated.remove(key, new SimpleCallbackWrapper() {

                @Override
                public void onFailure(final Throwable t) {
                    callback.onFailure(t);
                }

                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }
            });
        }

    }

    @Override
    public void put(final K key, final V value, final SimpleCallback callback) {

        if (ENABLE_LOG) {
            System.out.println(this + ": Put " + key + " " + value);
        }

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

    @Override
    public void putSync(final K key, final V value) {
        AsyncJre.waitFor(new Operation<Success>() {

            @Override
            public void apply(final ValueCallback<Success> callback) {
                put(key, value, new SimpleCallbackWrapper() {

                    @Override
                    public void onFailure(final Throwable t) {
                        callback.onFailure(t);
                    }

                    @Override
                    public void onSuccess() {
                        callback.onSuccess(Success.INSTANCE);
                    }
                });
            }
        });

    }

    @Override
    public void removeSync(final K key) {
        AsyncJre.waitFor(new Operation<Success>() {

            @Override
            public void apply(final ValueCallback<Success> callback) {
                remove(key, new SimpleCallbackWrapper() {

                    @Override
                    public void onFailure(final Throwable t) {
                        callback.onFailure(t);
                    }

                    @Override
                    public void onSuccess() {
                        callback.onSuccess(Success.INSTANCE);
                    }
                });
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void get(final K key, final ValueCallback<V> callback) {
        final Object value = pendingPuts.get(key);

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
    public V getSync(final K key) {

        final Object value = pendingPuts.get(key);

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
                writeValue(key, new SimpleCallbackWrapper() {

                    @Override
                    public void onFailure(final Throwable t) {
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

        commit(new SimpleCallbackWrapper() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                decorated.stop(new SimpleCallbackWrapper() {

                    @Override
                    public void onFailure(final Throwable t) {
                        callback.onFailure(t);
                    }

                    @Override
                    public void onSuccess() {
                        new Thread() {

                            @Override
                            public void run() {
                                executor.shutdown();

                                try {
                                    executor.awaitTermination(60000, TimeUnit.MILLISECONDS);
                                } catch (final InterruptedException e) {
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
    public void start(final SimpleCallback callback) {
        this.executor = Executors.newFixedThreadPool(this.workerThreads, new ThreadFactory() {

            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(r, this.getClass() + "->worker");
            }
        });
        this.pendingPuts = new ConcurrentHashMap<K, Object>();
        callback.onSuccess();
    }

    @Override
    public void commit(final SimpleCallback callback) {

        new Thread() {

            @Override
            public void run() {

                while (pendingPuts.size() > 0) {
                    try {
                        Thread.sleep(10);
                    } catch (final InterruptedException e) {
                        callback.onFailure(e);
                        return;
                    }
                }

                decorated.commit(callback);
            }

        }.start();

    }

    @Override
    public void performOperation(final MapOperation operation) {
        decorated.performOperation(operation);
    }

    public SplitWorkerThreadsMapConnection(final AsyncMap<K, V> connection, final int workerThreads) {
        super();
        this.decorated = connection;
        this.workerThreads = workerThreads;

    }

}
