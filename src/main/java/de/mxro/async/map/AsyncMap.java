package de.mxro.async.map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;

public interface AsyncMap<K, V> {

	public void put(K key, V value, SimpleCallback callback);

	public void get(K key, ValueCallback<V> callback);

	public void remove(K key, SimpleCallback callback);

}
