package de.mxro.async.map.tests;

import one.utils.jre.concurrent.JreConcurrency;

import org.junit.Test;

import de.mxro.async.Async;
import de.mxro.async.Deferred;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.jre.AsyncJre;
import de.mxro.async.map.AsyncMap;
import de.mxro.async.map.AsyncMaps;
import de.mxro.fn.Success;

public class TestThatAsynchronousPutMapCanBeStopped {

    @Test
    public void test() {

        final AsyncMap<String, String> map = AsyncMaps.enforceAsynchronousPut(10, new JreConcurrency(),
                AsyncMaps.<String, String> hashMap());

        AsyncJre.waitFor(new Deferred<Success>() {

            @Override
            public void get(final ValueCallback<Success> callback) {
                map.put("1", "one", Async.wrap(callback));
            }
        });

        AsyncJre.waitFor(new Deferred<Success>() {

            @Override
            public void get(final ValueCallback<Success> callback) {
                map.put("2", "two", Async.wrap(callback));
            }
        });

        AsyncJre.waitFor(new Deferred<Success>() {

            @Override
            public void get(final ValueCallback<Success> callback) {
                map.stop(Async.wrap(callback));
            }
        });

    }
}
