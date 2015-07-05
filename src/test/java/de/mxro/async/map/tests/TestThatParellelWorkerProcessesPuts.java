package de.mxro.async.map.tests;

import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.AsyncMaps;
import de.mxro.async.map.jre.AsyncMapsJre;
import delight.async.AsyncCommon;
import delight.async.Operation;
import delight.async.callbacks.SimpleCallback;
import delight.async.callbacks.ValueCallback;
import delight.async.jre.Async;
import delight.functional.Success;
import org.junit.Test;

@SuppressWarnings("all")
public class TestThatParellelWorkerProcessesPuts {
  @Test
  public void test() {
    AsyncMap<String, String> _hashMap = AsyncMaps.<String, String>hashMap();
    final AsyncMap<String, String> map = AsyncMapsJre.<String, String>divideWork(4, _hashMap);
    final Operation<Success> _function = new Operation<Success>() {
      @Override
      public void apply(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = AsyncCommon.wrap(callback);
        map.start(_wrap);
      }
    };
    Async.<Success>waitFor(_function);
    final Operation<Success> _function_1 = new Operation<Success>() {
      @Override
      public void apply(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = AsyncCommon.wrap(callback);
        map.put("1", "one", _wrap);
      }
    };
    Async.<Success>waitFor(_function_1);
    final Operation<Success> _function_2 = new Operation<Success>() {
      @Override
      public void apply(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = AsyncCommon.wrap(callback);
        map.put("1", "one", _wrap);
      }
    };
    Async.<Success>waitFor(_function_2);
    final Operation<Success> _function_3 = new Operation<Success>() {
      @Override
      public void apply(final ValueCallback<Success> callback) {
        SimpleCallback _wrap = AsyncCommon.wrap(callback);
        map.stop(_wrap);
      }
    };
    Async.<Success>waitFor(_function_3);
  }
}
