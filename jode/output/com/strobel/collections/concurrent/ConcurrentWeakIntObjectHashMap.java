/* ConcurrentWeakIntObjectHashMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.collections.concurrent;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import com.strobel.core.Comparer;

public final class ConcurrentWeakIntObjectHashMap
    extends ConcurrentRefValueIntObjectHashMap
{
    private static final class WeakIntReference extends WeakReference
	implements ConcurrentRefValueIntObjectHashMap.IntReference
    {
	private final int _hash;
	private final int _key;
	
	WeakIntReference(int key, Object referent, ReferenceQueue q) {
	    super(referent, q);
	    _key = key;
	    _hash = referent.hashCode();
	}
	
	public final int key() {
	    return _key;
	}
	
	public final int hashCode() {
	    return _hash;
	}
	
	public final boolean equals(Object obj) {
	label_1395:
	    {
		if (obj instanceof WeakIntReference) {
		    WeakIntReference other = (WeakIntReference) obj;
		    if (other._hash != _hash
			|| !Comparer.equals(other.get(), this.get()))
			PUSH false;
		    else
			PUSH true;
		} else
		    return false;
	    }
	    return POP;
	    break label_1395;
	}
    }
    
    protected final ConcurrentRefValueIntObjectHashMap.IntReference createReference
	(int key, Object value, ReferenceQueue queue) {
	return new WeakIntReference(key, value, queue);
    }
}
