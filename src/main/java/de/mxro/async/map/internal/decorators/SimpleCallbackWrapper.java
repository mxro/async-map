package de.mxro.async.map.internal.decorators;

import delight.async.callbacks.SimpleCallback;

/**
 * <p>
 * For some reason, proguard does not obfusticate the SimpleCallback anonymous
 * classes in the LazyStartup Map and EnforceAsynchronousPutMap.
 * <p>
 * Using this class instead of the interface SimpleCallback to instantiate the
 * anonoymous classes seems to solve the problem.
 * <p>
 * As a side effect, this also allows for more compact minimized code when
 * SimpleCallback is excluded from being obfusticated.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
public abstract class SimpleCallbackWrapper implements SimpleCallback {

}
