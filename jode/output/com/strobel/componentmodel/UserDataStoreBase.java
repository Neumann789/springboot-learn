/* UserDataStoreBase - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.componentmodel;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.strobel.core.ExceptionUtilities;

public class UserDataStoreBase implements UserDataStore, Cloneable
{
    public static final Key COPYABLE_USER_MAP_KEY
	= Key.create("COPYABLE_USER_MAP_KEY");
    private static final AtomicReferenceFieldUpdater UPDATER
	= AtomicReferenceFieldUpdater.newUpdater(UserDataStoreBase.class,
						 FrugalKeyMap.class, "_map");
    private volatile FrugalKeyMap _map = FrugalKeyMap.EMPTY;
    
    public Object getUserData(Key key) {
	return _map.get(key);
    }
    
    public void putUserData(Key key, Object value) {
	for (;;) {
	    FrugalKeyMap oldMap;
	    FrugalKeyMap newMap;
	label_1397:
	    {
		oldMap = _map;
		if (value != null)
		    newMap = oldMap.plus(key, value);
		else
		    newMap = oldMap.minus(key);
		break label_1397;
	    }
	    IF (newMap == oldMap
		|| UPDATER.compareAndSet(this, oldMap, newMap))
		/* empty */
	}
    }
    
    public Object putUserDataIfAbsent(Key key, Object value) {
	for (;;) {
	    FrugalKeyMap oldMap = _map;
	    Object oldValue = _map.get(key);
	    FrugalKeyMap newMap;
	label_1398:
	    {
		if (oldValue == null) {
		    if (value != null)
			newMap = oldMap.plus(key, value);
		    else
			newMap = oldMap.minus(key);
		} else
		    return oldValue;
	    }
	    if (newMap == oldMap
		|| UPDATER.compareAndSet(this, oldMap, newMap))
		return value;
	    break label_1398;
	}
    }
    
    public boolean replace(Key key, Object oldValue, Object newValue) {
	for (;;) {
	    FrugalKeyMap oldMap = _map;
	    Object currentValue = _map.get(key);
	    FrugalKeyMap newMap;
	label_1399:
	    {
		if (currentValue == oldValue) {
		    if (newValue != null)
			newMap = oldMap.plus(key, newValue);
		    else
			newMap = oldMap.minus(key);
		} else
		    return false;
	    }
	    if (newMap == oldMap
		|| UPDATER.compareAndSet(this, oldMap, newMap))
		return true;
	    break label_1399;
	}
    }
    
    public final UserDataStoreBase clone() {
	try {
	    return (UserDataStoreBase) super.clone();
	} catch (CloneNotSupportedException PUSH) {
	    CloneNotSupportedException e = POP;
	    throw ExceptionUtilities.asRuntimeException(e);
	}
    }
}
