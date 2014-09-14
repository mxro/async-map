package de.mxro.async.map.internal.decorators;

import de.mxro.async.callbacks.SimpleCallback;

final class EmptyCallback implements SimpleCallback {
    @Override
    public void onFailure(final Throwable arg0) {

    }

    @Override
    public void onSuccess() {

    }
}