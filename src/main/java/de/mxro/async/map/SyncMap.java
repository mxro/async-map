package de.mxro.async.map;

public interface SyncMap<K, V> {

	public V getSync(K key);

	public void putSync(K key, V value);

	public void removeSync(K key, V value);

}
