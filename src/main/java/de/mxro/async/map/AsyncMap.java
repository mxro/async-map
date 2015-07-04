package de.mxro.async.map;

import de.mxro.async.map.operations.ClearCacheOperation;
import de.mxro.async.map.operations.GetOperation;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.service.Service;
import delight.async.callbacks.SimpleCallback;
import delight.async.callbacks.ValueCallback;

/**
 * <p>
 * An interface for an asynchronous, persisted map.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 * @param <K>
 * @param <V>
 */
public interface AsyncMap<K, V> extends Service {

    public void put(K key, V value, SimpleCallback callback);

    public void putSync(K key, V value);

    public void get(K key, ValueCallback<V> callback);

    public V getSync(K key);

    public void remove(K key, SimpleCallback callback);

    public void removeSync(K key);

    /**
     * Assures that a connection to the persistence medium is established and
     * the map is operational.
     * 
     * @param callback
     */
    @Override
    public void start(SimpleCallback callback);

    /**
     * <p>
     * Releases resources held by this map.
     * <p>
     * Assures pending changes are written to persistence medium.
     * 
     * @param callback
     */
    @Override
    public void stop(SimpleCallback callback);

    /**
     * Assures pending changes are written to persistence medium.
     * 
     * @param callback
     */
    public void commit(SimpleCallback callback);

    /**
     * <p>
     * Perform a generic operation on this map.
     * <p>
     * For example, {@link ClearCacheOperation}, {@link GetOperation}
     */
    public void performOperation(MapOperation operation);

}
