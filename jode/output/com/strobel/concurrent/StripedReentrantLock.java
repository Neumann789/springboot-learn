/* StripedReentrantLock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.concurrent;
import java.util.concurrent.locks.ReentrantLock;

public final class StripedReentrantLock extends StripedLock
{
    private static final StripedReentrantLock INSTANCE
	= new StripedReentrantLock();
    
    public static StripedReentrantLock instance() {
	return INSTANCE;
    }
    
    public StripedReentrantLock() {
	super(ReentrantLock.class);
    }
    
    protected final ReentrantLock createLock() {
	return new ReentrantLock();
    }
    
    public final void lock(int index) {
	((ReentrantLock[]) locks)[index].lock();
    }
    
    public final void unlock(int index) {
	((ReentrantLock[]) locks)[index].unlock();
    }
}
