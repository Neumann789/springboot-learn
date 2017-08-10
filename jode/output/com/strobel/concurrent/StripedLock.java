/* StripedLock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.concurrent;
import java.lang.reflect.Array;

public abstract class StripedLock
{
    private static final int LOCK_COUNT = 256;
    protected final Object[] locks;
    private int _lockAllocationCounter;
    
    protected StripedLock(Class lockType) {
	locks = (Object[]) Array.newInstance(lockType, 256);
	int i = 0;
	for (;;) {
	    IF (i >= locks.length)
		/* empty */
	    locks[i] = createLock();
	    i++;
	}
    }
    
    public Object allocateLock() {
	return locks[allocateLockIndex()];
    }
    
    public int allocateLockIndex() {
	return _lockAllocationCounter = (_lockAllocationCounter + 1) % 256;
    }
    
    protected abstract Object createLock();
    
    public abstract void lock(int i);
    
    public abstract void unlock(int i);
}
