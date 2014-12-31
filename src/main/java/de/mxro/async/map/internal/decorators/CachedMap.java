package de.mxro.async.map.internal.decorators;

import java.util.Map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.fn.Fn;

class CachedMap<K, V> implements AsyncMap<K, V> {

    private final AsyncMap<K, V> decorated;
    private final AsyncMap<K, V> cache;

    private final static Object NULL = Fn.object();

    @Override
    public void put(final K key, final V value, final SimpleCallback callback) {
        if (value != null) {
            this.cache.put(key, value);
        } else {
            this.cache.put(key, NULL);
        }
        decorated.put(key, value, callback);
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

    @SuppressWarnings("unchecked")
    @Override
    public void get(final K key, final ValueCallback<V> callback) {
        final Object fromCache = this.cache.get(key);
        if (fromCache != null) {
            if (fromCache == NULL) {
                callback.onSuccess(null);
                return;
            } else {
                callback.onSuccess((V) fromCache);
                return;
            }

        }

        decorated.get(key, callback);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getSync(final K key) {
        final Object fromCache = this.cache.get(key);
        if (fromCache != null) {
            if (fromCache == NULL) {
                return null;
            } else {
                return (V) fromCache;
            }

        }

        return decorated.getSync(key);
    }

    @Override
    public void remove(final K key, final SimpleCallback callback) {
        this.cache.remove(key);
        this.decorated.remove(key, callback);
    }

    @Override
    public void removeSync(final K key) {
        this.cache.remove(key);
        this.decorated.removeSync(key);
    }

    @Override
    public void start(final SimpleCallback callback) {
        this.decorated.start(callback);
    }

    @Override
    public void stop(final SimpleCallback callback) {
        this.decorated.stop(callback);
    }

    @Override
    public void commit(final SimpleCallback callback) {
        this.decorated.commit(callback);
    }

    @Override
    public void performOperation(final MapOperation operation) {
        this.decorated.performOperation(operation);
    }

    public CachedMap(final Map<K, Object> cache, final AsyncMap<K, V> decorated) {
        super();
        this.decorated = decorated;
        this.cache = cache;
    }

}
