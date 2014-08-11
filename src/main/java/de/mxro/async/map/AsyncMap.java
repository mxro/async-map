package de.mxro.async.map;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;

public interface AsyncMap<T> {

	public void put(String key, T value, SimpleCallback callback);

	public void get(String key, ValueCallback<T> callback);

	public void remove(String key, SimpleCallback callback);

}
