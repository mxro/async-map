package de.mxro.async.map.tests;

import one.utils.jre.concurrent.JreConcurrency;

import org.junit.Test;

import de.mxro.async.map.AsyncMaps;

public class TestThatAsynchronousPutMapCanBeStopped {

    @Test
    public void test() {

        AsyncMaps.enforceAsynchronousPut(10, new JreConcurrency(), decorated)

    }
}
