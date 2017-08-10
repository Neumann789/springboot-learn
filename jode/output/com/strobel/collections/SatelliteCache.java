/* SatelliteCache - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.collections;
import java.util.HashMap;

final class SatelliteCache extends Cache
{
    private final Cache _parent;
    private final HashMap _cache;
    
    public SatelliteCache() {
	_cache = new HashMap();
	_parent = null;
    }
    
    public Cache getSatelliteCache() {
	return this;
    }
    
    public boolean replace(Object key, Object expectedValue,
			   Object updatedValue) {
	if (_parent == null
	    || _parent.replace(key, expectedValue, updatedValue)) {
	    _cache.put(key, updatedValue);
	    return true;
	}
	return false;
    }
    
    public SatelliteCache(Cache parent) {
	_cache = new HashMap();
	_parent = parent;
    }
    
    public Object cache(Object key, Object value) {
	Object cachedValue = _cache.get(key);
    label_1372:
	{
	    if (cachedValue == null) {
		if (_parent == null)
		    cachedValue = value;
		else
		    cachedValue = _parent.cache(key, value);
	    } else
		return cachedValue;
	}
	_cache.put(key, cachedValue);
	return cachedValue;
	break label_1372;
    }
    
    public Object get(Object key) {
	Object cachedValue = _cache.get(key);
    label_1373:
	{
	    if (cachedValue == null) {
		if (_parent != null) {
		    cachedValue = _parent.get(key);
		    if (cachedValue != null)
			_cache.put(key, cachedValue);
		}
	    } else
		return cachedValue;
	}
	return cachedValue;
	break label_1373;
    }
}
