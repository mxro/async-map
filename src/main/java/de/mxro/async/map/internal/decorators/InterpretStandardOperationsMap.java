package de.mxro.async.map.internal.decorators;

import javax.management.PersistentMBean;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.operations.GetOperation;
import de.mxro.async.map.operations.MapOperation;
import de.mxro.async.map.operations.PutOperation;
import de.mxro.async.map.operations.RemoveOperation;

public class InterpretStandardOperationsMap<K, V> implements AsyncMap<K, V> {

	private final AsyncMap<K, V> decorated;

	@Override
	public void put(K key, V value, SimpleCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void get(K key, ValueCallback<V> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(K key, SimpleCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public V getSync(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putSync(K key, V value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSync(K key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(SimpleCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(SimpleCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit(SimpleCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performOperation(MapOperation operation) {
		if (operation instanceof GetOperation) {

			@SuppressWarnings("unchecked")
			GetOperation<K, V> getOperation = (GetOperation<K, V>) operation;
			this.get(getOperation.getKey(), getOperation.getCallback());
			return;
		}

		if (operation instanceof PutOperation) {
			@SuppressWarnings("unchecked")
			PutOperation<K, V> putOperation = (PutOperation<K, V>) operation;

			this.put(putOperation.getKey(), putOperation.getValue(),
					putOperation.getCallback());
			return;
		}
		
		if (operation instanceof RemoveOperation) {
			@SuppressWarnings("unchecked")
			RemoveOperation<K> removeOperation = (RemoveOperation<K>) operation;
			
			this.remove(removeOperation.getKey(), removeOperation.get);
			
		}
	}

	public InterpretStandardOperationsMap(AsyncMap<K, V> decorated) {
		super();
		this.decorated = decorated;
	}

}
