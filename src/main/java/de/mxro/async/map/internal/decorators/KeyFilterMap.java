package de.mxro.async.map.internal.decorators;

import delight.async.callbacks.SimpleCallback;
import delight.async.callbacks.ValueCallback;
import delight.functional.Function;

import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.MapOperation;

/**
 * <P>
 * Allows to apply a simple filter on keys before they are passed onto the
 * decorated connection.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
class KeyFilterMap<K, V> implements AsyncMap<K, V> {

    private final Function<K, K> filter;
    private final AsyncMap<K, V> decorated;

    @Override
    public void put(final K key, final V value, final SimpleCallback callback) {
        this.decorated.put(filter.apply(key), value, callback);
    }

    @Override
    public void get(final K key, final ValueCallback<V> callback) {
        this.decorated.get(filter.apply(key), callback);
    }

    @Override
    public void remove(final K key, final SimpleCallback callback) {
        this.decorated.remove(filter.apply(key), callback);
    }

    @Override
    public V getSync(final K key) {
        return this.decorated.getSync(filter.apply(key));
    }

    @Override
    public void putSync(final K key, final V value) {
        this.decorated.putSync(filter.apply(key), value);
    }

    @Override
    public void removeSync(final K key) {
        this.decorated.removeSync(filter.apply(key));
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

    public KeyFilterMap(final Function<K, K> filter, final AsyncMap<K, V> decorated) {
        super();
        this.filter = filter;
        this.decorated = decorated;
    }

}
