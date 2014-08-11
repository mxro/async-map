package de.mxro.async.map;

/**
 * A map which can be modified using synchronous operations.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 * @param <K>
 * @param <V>
 */
public interface PureSyncMap<K, V> {

	public V getSync(K key);

	public void putSync(K key, V value);

	public void removeSync(K key, V value);

}
