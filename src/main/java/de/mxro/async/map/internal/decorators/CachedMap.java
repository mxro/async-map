package de.mxro.async.map.internal.decorators;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;

class CachedMap<K, V> implements AsyncMap<K, V> {

    private final AsyncMap<K, V> decorated;
    private final AsyncMap<K, V> cache;

    @Override
    public void put(final K key, final V value, final SimpleCallback callback) {

        decorated.put(key, value, new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                cache.put(key, value, callback);
            }
        });
    }

    @Override
    public void putSync(final K key, final V value) {
        if (value != null) {
            this.cache.putSync(key, value);
        } else {
            this.cache.putSync(key, value);
        }

        decorated.putSync(key, value);
    }

    @Override
    public void get(final K key, final ValueCallback<V> callback) {
        this.cache.get(key, new ValueCallback<V>() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess(final V value) {
                if (value != null) {
                    callback.onSuccess(value);
                    return;
                }

                decorated.get(key, new ValueCallback<V>() {

                    @Override
                    public void onFailure(final Throwable t) {
                        callback.onFailure(t);
                    }

                    @Override
                    public void onSuccess(final V value) {
                        callback.onSuccess(value);

                        // placing value in cache
                        cache.put(key, value, new SimpleCallback() {

                            @Override
                            public void onFailure(final Throwable t) {
                                throw new RuntimeException(t);
                            }

                            @Override
                            public void onSuccess() {

                            }
                        });

                    }
                });

            }
        });

    }

    @SuppressWarnings("unchecked")
    @Override
    public V getSync(final K key) {
        final Object fromCache = this.cache.getSync(key);
        if (fromCache != null) {

            return (V) fromCache;

        }

        return decorated.getSync(key);
    }

    @Override
    public void remove(final K key, final SimpleCallback callback) {

        this.decorated.remove(key, new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                cache.remove(key, callback);
            }
        });
    }

    @Override
    public void removeSync(final K key) {
        this.cache.removeSync(key);
        this.decorated.removeSync(key);
    }

    @Override
    public void start(final SimpleCallback callback) {
        this.decorated.start(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                cache.start(callback);
            }
        });
    }

    @Override
    public void stop(final SimpleCallback callback) {
        this.decorated.stop(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                cache.stop(callback);
            }
        });
    }

    @Override
    public void commit(final SimpleCallback callback) {
        this.decorated.commit(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                cache.commit(callback);
            }
        });
    }

    @Override
    public void performOperation(final MapOperation operation) {
        this.decorated.performOperation(operation);
        this.cache.performOperation(operation);
    }

    public CachedMap(final AsyncMap<K, V> cache, final AsyncMap<K, V> decorated) {
        super();
        this.decorated = decorated;
        this.cache = cache;
    }

}
