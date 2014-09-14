package de.mxro.async.map.internal.decorators;

import de.mxro.async.callbacks.SimpleCallback;

/**
 * For some reason, proguard does not obfusticate the SimpleCallback anonymous
 * classes in the LazyStartup Map. That's why I use this wrapper class.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
public abstract class SimpleCallbackWrapper implements SimpleCallback {

}
