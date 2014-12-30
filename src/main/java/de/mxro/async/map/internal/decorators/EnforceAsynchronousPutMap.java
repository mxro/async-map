package de.mxro.async.map.internal.decorators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import de.mxro.async.Value;
import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.flow.CallbackLatch;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.async.map.operations.PutOperation;
import de.mxro.concurrency.Concurrency;
import de.mxro.concurrency.wrappers.SimpleTimer;
import de.mxro.fn.Closure;

class EnforceAsynchronousPutMap<K, V> implements AsyncMap<K, V> {

    private final boolean ENABLE_LOG = true;

    private final AsyncMap<K, V> decorated;
    private final int delay;
    private final Concurrency concurrency;
    private final Map<K, List<PutOperation<K, V>>> pendingPuts;

    private final Value<Boolean> timerActive = new Value<Boolean>(false);
    private SimpleTimer timer = null;

    private final Value<Boolean> isShutdown = new Value<Boolean>(false);

    private final Vector<Closure<Object>> callWhenAllPutsProcessed = new Vector<Closure<Object>>();

    private final Value<Boolean> processing = new Value<Boolean>(false);

    private final List<SimpleCallback> pendingProcessRequests = new LinkedList<SimpleCallback>();

    private final static SimpleCallback EMPTY_CALLBACK = new EmptyCallback();

    /*
     * private final static Object NULL = new Object() {
     * 
     * };
     */

    @Override
    public void put(final K key, final V value, final SimpleCallback callback) {
        if (ENABLE_LOG) {
            System.out.println(this + ": Put " + key + " " + value);

        }

        if (isShutdown.get()) {
            // callback.onSuccess();
            callback.onFailure(new Exception("Cannot put node into shut down map."));
            return;
        }

        synchronized (pendingPuts) {

            if (!pendingPuts.containsKey(key)) {
                pendingPuts.put(key, new LinkedList<PutOperation<K, V>>());
            }

            final PutOperation<K, V> putOperation = new PutOperation<K, V>(key, value, callback);

            pendingPuts.get(key).add(putOperation);

        }

        synchronized (timerActive) {
            if (timerActive.get() == true) {
                return;
            }

            timerActive.set(true);

            timer = concurrency.newTimer().scheduleOnce(delay, new Runnable() {
                @Override
                public void run() {
                    timer.stop();
                    timer = null;

                    timerActive.set(false);
                    processPuts(EMPTY_CALLBACK);
                }
            });

        }
    }

    private final void processPuts(final SimpleCallback callback) {
        if (ENABLE_LOG) {
            System.out.println(this + ": Test if puts need to be processed");
        }

        synchronized (processing) {
            if (processing.get()) {
                synchronized (pendingProcessRequests) {
                    if (ENABLE_LOG) {
                        System.out.println(this + ": Defer processing");
                    }
                    pendingProcessRequests.add(callback);
                    return;
                }
            }
            processing.set(true);
        }

        final Map<K, List<PutOperation<K, V>>> puts;
        boolean putsEmpty = false;
        synchronized (pendingPuts) {
            if (pendingPuts.size() == 0) {
                putsEmpty = true;
                puts = null;
            } else {

                puts = new HashMap<K, List<PutOperation<K, V>>>(pendingPuts);

                pendingPuts.clear();

            }
        }

        if (putsEmpty) {
            if (ENABLE_LOG) {
                System.out.println(this + ": Nothing to process");
            }

            processing.set(false);

            callback.onSuccess();

            triggerPendingProcessOperations();

            return;
        }

        final CallbackLatch latch = new CallbackLatch(puts.size()) {

            @Override
            public void onFailed(final Throwable t) {

                processing.set(false);

                callback.onFailure(t);
                triggerPendingProcessOperations();
            }

            @Override
            public void onCompleted() {
                if (ENABLE_LOG) {
                    System.out.println(this + ": All processed");
                }

                processing.set(false);

                callback.onSuccess();
                triggerPendingProcessOperations();
            }
        };
        if (ENABLE_LOG) {
            System.out.println(this + ": Puts to process " + puts.entrySet());
        }

        for (final Entry<K, List<PutOperation<K, V>>> put : puts.entrySet()) {

            decorated.put(put.getKey(), put.getValue().get(put.getValue().size() - 1).getValue(),
                    new SimpleCallbackWrapper() {

                @Override
                public void onFailure(final Throwable arg0) {
                    for (final PutOperation<K, V> operation : put.getValue()) {
                        operation.getCallback().onFailure(arg0);
                    }
                    latch.registerSuccess();
                }

                @Override
                public void onSuccess() {
                    for (final PutOperation<K, V> operation : put.getValue()) {
                        operation.getCallback().onSuccess();
                    }
                    latch.registerSuccess();
                }
            });
        }

    }

    private void triggerPendingProcessOperations() {
        synchronized (pendingProcessRequests) {
            final ArrayList<SimpleCallback> pending = new ArrayList<SimpleCallback>(pendingProcessRequests);
            pendingProcessRequests.clear();
            for (final SimpleCallback pendingOperation : pending) {
                processPuts(pendingOperation);
            }
        }
    }

    @Override
    public void get(final K key, final ValueCallback<V> callback) {
        synchronized (pendingPuts) {
            if (pendingPuts.containsKey(key)) {
                callback.onSuccess(pendingPuts.get(key).get(pendingPuts.get(key).size() - 1).getValue());
                return;
            }
        }
        decorated.get(key, callback);
    }

    @Override
    public V getSync(final K key) {
        synchronized (pendingPuts) {
            if (pendingPuts.containsKey(key)) {
                return pendingPuts.get(key).get(pendingPuts.get(key).size() - 1).getValue();
            }
        }

        return decorated.getSync(key);
    }

    @Override
    public void putSync(final K key, final V value) {
        throw new RuntimeException("Synchronized put not supported on delayed put connection.");
    }

    @Override
    public void removeSync(final K key) {

        synchronized (pendingPuts) {
            pendingPuts.remove(key);
        }

        decorated.removeSync(key);
    }

    @Override
    public void remove(final K key, final SimpleCallback callback) {
        synchronized (pendingPuts) {
            pendingPuts.remove(key);
        }
        decorated.remove(key, callback);
    }

    private final void processAllPuts(final SimpleCallback callback) {
        processPuts(new SimpleCallbackWrapper() {

            @Override
            public void onFailure(final Throwable arg0) {
                callback.onFailure(arg0);
            }

            @Override
            public void onSuccess() {
                synchronized (pendingPuts) {
                    if (pendingPuts.size() == 0) {
                        callback.onSuccess();

                        final ArrayList<Closure<Object>> copy = new ArrayList<Closure<Object>>();
                        synchronized (callWhenAllPutsProcessed) {
                            copy.addAll(callWhenAllPutsProcessed);

                            callWhenAllPutsProcessed.clear();
                        }

                        for (final Closure<Object> func : copy) {
                            func.apply(null);
                        }

                        return;
                    }
                }

                processAllPuts(callback);

            }
        });
    }

    @Override
    public void start(final SimpleCallback callback) {
        if (ENABLE_LOG) {
            System.out.println(this + ": Starting");

        }

        this.isShutdown.set(false);

        decorated.start(callback);
    }

    @Override
    public void stop(final SimpleCallback callback) {
        if (ENABLE_LOG) {
            System.out.println(this + ": Stopping");
        }

        // new RuntimeException("Stopping it.").printStackTrace();

        processAllPuts(new SimpleCallbackWrapper() {

            @Override
            public void onFailure(final Throwable arg0) {
                callback.onFailure(arg0);
            }

            @Override
            public void onSuccess() {

                if (timerActive.get() || processing.get()) {

                    callWhenAllPutsProcessed.add(new Closure<Object>() {

                        @Override
                        public void apply(final Object o) {
                            final SimpleTimer scheduleOnce = concurrency.newTimer().scheduleOnce(10, new Runnable() {

                                @Override
                                public void run() {
                                    stop(callback);
                                }
                            });
                            scheduleOnce.stop();
                        }
                    });

                    return;
                }

                isShutdown.set(true);

                if (ENABLE_LOG) {
                    System.out.println(EnforceAsynchronousPutMap.this + ": Stopped");
                }
                decorated.stop(callback);
            }
        });

    }

    @Override
    public void commit(final SimpleCallback callback) {
        processAllPuts(new SimpleCallbackWrapper() {

            @Override
            public void onFailure(final Throwable arg0) {
                callback.onFailure(arg0);
            }

            @Override
            public void onSuccess() {
                decorated.commit(callback);
            }
        });

    }

    @Override
    public void performOperation(final MapOperation operation) {
        decorated.performOperation(operation);
    }

    public EnforceAsynchronousPutMap(final int delay, final Concurrency con, final AsyncMap<K, V> decorated) {
        super();
        this.decorated = decorated;
        this.delay = delay;
        this.concurrency = con;
        this.pendingPuts = new HashMap<K, List<PutOperation<K, V>>>();
    }

}
