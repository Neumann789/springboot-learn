/* JavosizeHashMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.agent;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavosizeHashMap extends LinkedHashMap
{
    private static final long serialVersionUID = -3216599441788189122L;
    private int maxCapacity = 1000;
    
    protected JavosizeHashMap(int capacity) {
	super(capacity);
	maxCapacity = capacity;
    }
    
    protected JavosizeHashMap(int capacity, boolean accessOrder) {
	super(capacity, 0.75F, accessOrder);
	maxCapacity = capacity;
    }
    
    protected boolean removeEldestEntry(Map.Entry eldest) {
	return size() >= maxCapacity;
    }
}
