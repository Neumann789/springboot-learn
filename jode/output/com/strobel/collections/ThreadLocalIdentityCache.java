/* ThreadLocalIdentityCache - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.collections;

final class ThreadLocalIdentityCache extends Cache
{
    private final Cache _parent;
    private final ThreadLocal _threadCaches;
    
    public ThreadLocalIdentityCache() {
	_threadCaches = new ThreadLocal() {
	    {
		super();
	    }
	    
	    protected SatelliteCache initialValue() {
		return new SatelliteCache(_parent);
	    }
	};
	_parent = null;
    }
    
    public Cache getSatelliteCache() {
	return (Cache) _threadCaches.get();
    }
    
    public boolean replace(Object key, Object expectedValue,
			   Object updatedValue) {
	return ((SatelliteCache) _threadCaches.get())
		   .replace(key, expectedValue, updatedValue);
    }
    
    public ThreadLocalIdentityCache(Cache parent) {
	_threadCaches = new ThreadLocal() {
	    {
		super();
	    }
	    
	    protected SatelliteCache initialValue() {
		return new SatelliteCache(_parent);
	    }
	};
	_parent = parent;
    }
    
    public Object cache(Object key, Object value) {
	return ((SatelliteCache) _threadCaches.get()).cache(key, value);
    }
    
    public Object get(Object key) {
	return ((SatelliteCache) _threadCaches.get()).get(key);
    }
}
