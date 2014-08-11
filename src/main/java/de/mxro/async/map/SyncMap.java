package de.mxro.async.map;

public interface SyncMap<V> {

	public V getSync(String key);

	public void putSync(String key, V value);

	public void removeSync(String key, V value);

}
