package de.mxro.async.map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;

public interface AsyncMap<V> {

	public void put(String key, V value, SimpleCallback callback);

	public void get(String key, ValueCallback<V> callback);

	public void remove(String key, SimpleCallback callback);

}
