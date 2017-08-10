/* ConcurrentIntObjectMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.collections.concurrent;

public interface ConcurrentIntObjectMap
{
    public Object addOrGet(int i, Object object);
    
    public boolean remove(int i, Object object);
    
    public boolean replace(int i, Object object, Object object_0_);
    
    public Object put(int i, Object object);
    
    public Object putIfAbsent(int i, Object object);
    
    public Object get(int i);
    
    public Object remove(int i);
    
    public int size();
    
    public boolean isEmpty();
    
    public boolean contains(int i);
    
    public void clear();
    
    public int[] keys();
    
    public Iterable entries();
}
