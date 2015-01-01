package de.mxro.async.map.internal.decorators;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;

class TieredCachesMap<K, V> implements AsyncMap<K, V> {

    private final AsyncMap<K, V> secondaryCache;
    private final AsyncMap<K, V> primaryCache;

    @Override
    public void put(final K key, final V value, final SimpleCallback callback) {

        primaryCache.put(key, value, new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess();

                secondaryCache.put(key, value, new SimpleCallback() {

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

    @Override
    public void putSync(final K key, final V value) {
        if (value != null) {
            this.primaryCache.putSync(key, value);
        } else {
            this.primaryCache.putSync(key, value);
        }

        secondaryCache.putSync(key, value);
    }

    @Override
    public void get(final K key, final ValueCallback<V> callback) {
        this.primaryCache.get(key, new ValueCallback<V>() {

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

                secondaryCache.get(key, new ValueCallback<V>() {

                    @Override
                    public void onFailure(final Throwable t) {
                        callback.onFailure(t);
                    }

                    @Override
                    public void onSuccess(final V value) {
                        callback.onSuccess(value);

                        if (value == null) {
                            return;
                        }

                        // placing value in cache
                        primaryCache.put(key, value, new SimpleCallback() {

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
        final Object fromCache = this.primaryCache.getSync(key);
        if (fromCache != null) {

            return (V) fromCache;

        }

        return secondaryCache.getSync(key);
    }

    @Override
    public void remove(final K key, final SimpleCallback callback) {

        this.secondaryCache.remove(key, new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                primaryCache.remove(key, callback);
            }
        });
    }

    @Override
    public void removeSync(final K key) {
        this.primaryCache.removeSync(key);
        this.secondaryCache.removeSync(key);
    }

    @Override
    public void start(final SimpleCallback callback) {
        this.secondaryCache.start(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                primaryCache.start(callback);
            }
        });
    }

    @Override
    public void stop(final SimpleCallback callback) {
        this.secondaryCache.stop(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                primaryCache.stop(callback);
            }
        });
    }

    @Override
    public void commit(final SimpleCallback callback) {
        this.secondaryCache.commit(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                primaryCache.commit(callback);
            }
        });
    }

    @Override
    public void performOperation(final MapOperation operation) {
        this.secondaryCache.performOperation(operation);
        this.primaryCache.performOperation(operation);
    }

    public TieredCachesMap(final AsyncMap<K, V> cache, final AsyncMap<K, V> decorated) {
        super();
        this.secondaryCache = decorated;
        this.primaryCache = cache;
    }

}
