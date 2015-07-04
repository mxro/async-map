package de.mxro.async.map.internal.decorators;

import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;
import delight.async.callbacks.SimpleCallback;
import delight.async.callbacks.ValueCallback;

/**
 * <p>
 * Use in case seralizations can get outdated in a cache.
 * 
 * @author <a href="http://www.mxro.de/">Max Rohde</a>
 * 
 */
class PurgeInvalidValuesMap<K, V> implements AsyncMap<K, V> {

    private final AsyncMap<K, V> decorated;

    @Override
    public void put(final K key, final V value, final SimpleCallback callback) {
        decorated.put(key, value, callback);
    }

    @Override
    public void get(final K key, final ValueCallback<V> callback) {
        get(key, callback, true);
    }

    private final void get(final K key, final ValueCallback<V> callback, final boolean firstTry) {
        try {
            decorated.get(key, callback);
        } catch (final Throwable t) {
            if (!firstTry) {
                throw new RuntimeException(t);
            }

            deleteAndReget(key, callback);
        }

    }

    private final void deleteAndReget(final K key, final ValueCallback<V> callback) {
        remove(key, new SimpleCallbackWrapper() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                get(key, callback, false);
            }
        });

    }

    @Override
    public void remove(final K key, final SimpleCallback callback) {
        decorated.remove(key, callback);
    }

    @Override
    public V getSync(final K key) {
        try {
            final V res = decorated.getSync(key);

            return res;
        } catch (final Throwable t) {
            return removeAndRegetSync(key);
        }

    }

    public V removeAndRegetSync(final K key) {
        remove(key, new SimpleCallbackWrapper() {

            @Override
            public void onFailure(final Throwable t) {
                throw new RuntimeException(t);
            }

            @Override
            public void onSuccess() {

            }
        });

        return decorated.getSync(key);
    }

    @Override
    public void putSync(final K key, final V value) {
        decorated.putSync(key, value);
    }

    @Override
    public void removeSync(final K key) {
        decorated.removeSync(key);
    }

    @Override
    public void start(final SimpleCallback callback) {
        decorated.start(callback);
    }

    @Override
    public void stop(final SimpleCallback callback) {
        decorated.stop(callback);
    }

    @Override
    public void commit(final SimpleCallback callback) {
        decorated.commit(callback);
    }

    @Override
    public void performOperation(final MapOperation operation) {
        decorated.performOperation(operation);
    }

    public PurgeInvalidValuesMap(final AsyncMap<K, V> decorated) {
        super();
        this.decorated = decorated;
    }

}
