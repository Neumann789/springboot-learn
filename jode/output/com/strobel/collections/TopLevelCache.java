/* TopLevelCache - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.collections;
import java.util.concurrent.ConcurrentHashMap;

final class TopLevelCache extends Cache
{
    private final ConcurrentHashMap _cache = new ConcurrentHashMap();
    
    public Object cache(Object key, Object value) {
    label_1382:
	{
	    Object cachedValue = _cache.putIfAbsent(key, value);
	    if (cachedValue == null)
		PUSH value;
	    else
		PUSH cachedValue;
	    break label_1382;
	}
	return POP;
    }
    
    public Cache getSatelliteCache() {
	return createSatelliteCache(this);
    }
    
    public boolean replace(Object key, Object expectedValue,
			   Object updatedValue) {
    label_1383:
	{
	    if (expectedValue != null)
		return _cache.replace(key, expectedValue, updatedValue);
	    if (_cache.putIfAbsent(key, updatedValue) != null)
		PUSH false;
	    else
		PUSH true;
	    break label_1383;
	}
	return POP;
    }
    
    public Object get(Object key) {
	return _cache.get(key);
    }
}
