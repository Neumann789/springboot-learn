/* CallableSet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.github.jamm;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.Callable;

public class CallableSet implements Callable
{
    public Set call() throws Exception {
	return Collections.newSetFromMap(new IdentityHashMap());
    }
    
    public volatile Object call() throws Exception {
	return call();
    }
}
