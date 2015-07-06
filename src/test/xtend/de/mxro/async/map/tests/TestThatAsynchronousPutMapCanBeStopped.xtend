package de.mxro.async.map.tests

import de.mxro.async.map.AsyncMaps
import delight.concurrency.jre.JreConcurrency
import delight.async.AsyncCommon
import delight.async.jre.Async
import org.junit.Test

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
