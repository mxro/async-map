package de.mxro.async.map.tests

import de.mxro.async.map.AsyncMaps
import de.mxro.concurrency.jre.JreConcurrency
import org.junit.Test
import de.mxro.async.AsyncCommon
import de.mxro.async.jre.Async

class TestThatAsynchronousPutMapCanBeStopped {

	@Test
	def void test() {

		val map = AsyncMaps.enforceAsynchronousPut(10, new JreConcurrency(),
			AsyncMaps.<String, String>hashMap());

		Async.waitFor [ callback |
			map.start(AsyncCommon.wrap(callback));
		]

		Async.waitFor [ callback |
			map.put("1", "one", AsyncCommon.wrap(callback));
		]

		Async.waitFor [ callback |
			map.put("2", "two", AsyncCommon.wrap(callback));
		]

		Async.waitFor [ callback |
			map.stop(AsyncCommon.wrap(callback));
		]

	}
	

}
