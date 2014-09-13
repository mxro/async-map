package de.mxro.async.map.tests;

import de.mxro.async.Async;
import de.mxro.async.Deferred;
import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.jre.AsyncJre;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.AsyncMaps;
import de.mxro.async.map.jre.AsyncMapsJre;
import de.mxro.fn.Success;
import org.junit.Test;

@SuppressWarnings("all")
public class TestThatParellelWorkerProcessesPuts {
  @Test
  public void test() {
    AsyncMap<String, String> _hashMap = AsyncMaps.<String, String>hashMap();
    final AsyncMap<String, String> map = AsyncMapsJre.<String, String>divideWork(4, _hashMap);
    final Deferred<Success> _function = new Deferred<Success>() {
      public void get(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = Async.wrap(callback);
        map.put("1", "one", _wrap);
      }
    };
    AsyncJre.<Success>waitFor(_function);
    final Deferred<Success> _function_1 = new Deferred<Success>() {
      public void get(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = Async.wrap(callback);
        map.stop(_wrap);
      }
    };
    AsyncJre.<Success>waitFor(_function_1);
  }
}
