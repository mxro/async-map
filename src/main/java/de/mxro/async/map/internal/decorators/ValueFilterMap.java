package de.mxro.async.map.internal.decorators;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.fn.Function;

/**
 * <P>
 * Allows to apply a simple filter on keys before they are passed onto the
 * decorated connection.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
class ValueFilterMap<K, V> implements AsyncMap<K, V> {

    private final Function<V, V> beforeStorage;
    private final AsyncMap<K, V> decorated;

    @Override
    public void put(final K key, final V value, final SimpleCallback callback) {
        this.decorated.put(key, beforeStorage.apply(value), callback);
    }

    @Override
    public void get(final K key, final ValueCallback<V> callback) {
        this.decorated.get(key, new ValueCallback<V>() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess(final V value) {
                callback.onSuccess(beforeStorage.apply(value));
            }
        });
    }

    @Override
    public void remove(final K key, final SimpleCallback callback) {
        this.decorated.remove(key, callback);
    }

    @Override
    public V getSync(final K key) {
        return beforeStorage.apply(this.decorated.getSync(key));
    }

    @Override
    public void putSync(final K key, final V value) {
        this.decorated.putSync(key, beforeStorage.apply(value));
    }

    @Override
    public void removeSync(final K key) {
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

    public ValueFilterMap(final Function<V, V> filter, final AsyncMap<K, V> decorated) {
        super();
        this.beforeStorage = filter;
        this.decorated = decorated;
    }

}
