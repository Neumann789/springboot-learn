/* CallerResolver - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.compilerservices;

public final class CallerResolver extends SecurityManager
{
    private static final CallerResolver CALLER_RESOLVER = new CallerResolver();
    private static final int CALL_CONTEXT_OFFSET = 3;
    
    protected Class[] getClassContext() {
	return super.getClassContext();
    }
    
    public static Class getCallerClass(int callerOffset) {
	return CALLER_RESOLVER.getClassContext()[3 + callerOffset];
    }
    
    public static int getContextSize(int callerOffset) {
	return CALLER_RESOLVER.getClassContext().length - callerOffset;
    }
    
    public static int getContextSize() {
	return getContextSize(3);
    }
}
