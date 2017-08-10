/* Fences - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public class Fences
{
    private static volatile int theVolatile;
    
    private Fences() {
	/* empty */
    }
    
    public static Object orderReads(Object ref) {
	int ignore = theVolatile;
	return ref;
    }
    
    public static Object orderWrites(Object ref) {
	theVolatile = 0;
	return ref;
    }
    
    public static Object orderAccesses(Object ref) {
	theVolatile = 0;
	return ref;
    }
    
    public static void reachabilityFence(Object ref) {
	if (ref != null) {
	    Object object;
	    MONITORENTER (object = ref);
	    try {
		MONITOREXIT object;
	    } finally {
		Object object_0_ = POP;
		MONITOREXIT object;
		throw object_0_;
	    }
	}
	return;
    }
}
