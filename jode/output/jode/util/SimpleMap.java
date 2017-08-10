/* SimpleMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.util;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SimpleMap extends AbstractMap
{
    private Set backing;
    
    public static class SimpleEntry implements Map.Entry
    {
	Object key;
	Object value;
	
	public SimpleEntry(Object object, Object object_0_) {
	    key = object;
	    value = object_0_;
	}
	
	public Object getKey() {
	    return key;
	}
	
	public Object getValue() {
	    return value;
	}
	
	public Object setValue(Object object) {
	    Object object_1_ = value;
	    value = object;
	    return object_1_;
	}
	
	public int hashCode() {
	    return key.hashCode() ^ value.hashCode();
	}
	
	public boolean equals(Object object) {
	    if (!(object instanceof Map.Entry))
		return false;
	label_1109:
	    {
		Map.Entry entry = (Map.Entry) object;
		if (!key.equals(entry.getKey())
		    || !value.equals(entry.getValue()))
		    PUSH false;
		else
		    PUSH true;
		break label_1109;
	    }
	    return POP;
	}
    }
    
    public SimpleMap() {
	backing = new SimpleSet();
    }
    
    public SimpleMap(int i) {
	backing = new SimpleSet(i);
    }
    
    public SimpleMap(Set set) {
	backing = set;
    }
    
    public Set entrySet() {
	return backing;
    }
    
    public Object put(Object object, Object object_2_) {
	Iterator iterator = backing.iterator();
	for (;;) {
	    if (!iterator.hasNext()) {
		backing.add(new SimpleEntry(object, object_2_));
		return null;
	    }
	    Map.Entry entry = (Map.Entry) iterator.next();
	    IF (!object.equals(entry.getKey()))
		/* empty */
	    return entry.setValue(object_2_);
	}
    }
}
