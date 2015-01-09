package de.mxro.async.map.tests;

import de.mxro.async.Async;
import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.jre.AsyncJre;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.AsyncMaps;
import de.mxro.async.promise.Deferred;
import de.mxro.concurrency.jre.JreConcurrency;
import de.mxro.fn.Success;
import org.junit.Test;

@SuppressWarnings("all")
public class TestThatAsynchronousPutMapCanBeStopped {
  @Test
  public void test() {
    JreConcurrency _jreConcurrency = new JreConcurrency();
    AsyncMap<String, String> _hashMap = AsyncMaps.<String, String>hashMap();
    final AsyncMap<String, String> map = AsyncMaps.<String, String>enforceAsynchronousPut(10, _jreConcurrency, _hashMap);
    final Deferred<Success> _function = new Deferred<Success>() {
      public void get(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = Async.wrap(callback);
        map.start(_wrap);
      }
    };
    AsyncJre.<Success>waitFor(_function);
    final Deferred<Success> _function_1 = new Deferred<Success>() {
      public void get(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = Async.wrap(callback);
        map.put("1", "one", _wrap);
      }
    };
    AsyncJre.<Success>waitFor(_function_1);
    final Deferred<Success> _function_2 = new Deferred<Success>() {
      public void get(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = Async.wrap(callback);
        map.put("2", "two", _wrap);
      }
    };
    AsyncJre.<Success>waitFor(_function_2);
    final Deferred<Success> _function_3 = new Deferred<Success>() {
      public void get(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = Async.wrap(callback);
        map.stop(_wrap);
      }
    };
    AsyncJre.<Success>waitFor(_function_3);
  }
}
