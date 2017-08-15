/* HttpSessionHelperFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.agent;
import java.util.Map;

import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.compiler.ClassLoaderInjector;

public class HttpSessionHelperFactory
{
    private static volatile Class helper;
    
    public static synchronized Class getSessionHelper() {
	if (helper == null)
	    loadHelper();
	return helper;
    }
    
    private static synchronized void loadHelper() {
	object = object_1_;
	break;
    }
}
